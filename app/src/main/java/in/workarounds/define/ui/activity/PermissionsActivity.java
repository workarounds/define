package in.workarounds.define.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import in.workarounds.define.R;
import in.workarounds.define.ui.adapter.PermissionsAdapter;

/**
 * Created by manidesto on 31/10/15.
 */
public class PermissionsActivity extends BaseActivity implements PermissionsAdapter.GrantClickListener, View.OnClickListener{
    public static final String DRAW_OVER_OTHER_APPS = "android.permission.DRAW_OVER_OTHER_APPS";
    private static final String[] REQUIRED = {DRAW_OVER_OTHER_APPS};
    private static final String[] OPTIONAL = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private interface IntentKey{
        String REQUIRED_PERMISSION = "required_permission";
        String FROM_SPLASH = "from_splash";
    }

    private interface RequestCode{
        int DRAW_OVER_OTHER_APPS = 1;
        int STORAGE = 2;
    }

    public static Intent newIntent(Context context, String requiredPermission){
        Intent intent=  new Intent(context, PermissionsActivity.class);
        intent.putExtra(IntentKey.REQUIRED_PERMISSION, requiredPermission);
        return intent;
    }

    public static Intent fromSplash(SplashActivity activity){
        Intent intent = new Intent(activity, PermissionsActivity.class);
        intent.putExtra(IntentKey.FROM_SPLASH, true);
        return intent;
    }

    private List<Permission> permissions;
    private PermissionsAdapter adapter;

    /*Permission sent in the intent*/
    private String requiredPermission;

    private View nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        extractPermissionInIntent();
        createPermissions();
        setupNextButton();

        adapter = new PermissionsAdapter();
        adapter.setGrantClickListener(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_permissions);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        adapter.setPermissions(permissions);
    }

    @Override
    protected String getToolbarTitle() {
        return getString(R.string.permissions_page_title);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissions();
    }

    @Override
    public void onGrantClicked(Permission permission) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (DRAW_OVER_OTHER_APPS.equals(permission.identifier)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, RequestCode.DRAW_OVER_OTHER_APPS);
            } else {
                ActivityCompat.requestPermissions(
                        this, new String[] {permission.identifier},
                        getRequestCode(permission.identifier)
                        );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        String identifier = permissions[0];
        if(!ActivityCompat.shouldShowRequestPermissionRationale(this, identifier)){
            showPermissionDialog(identifier);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showPermissionDialog(final String identifier){
        AlertDialog permissionDialog = new AlertDialog.Builder(this)
                .setTitle("Permission")
                .setMessage(getPermissionRequestMessage(identifier))
                .setPositiveButton(R.string.button_go_to_settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.parse("package:" + getPackageName());
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();
        permissionDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_next:
                next();
                break;
            default:
                break;
        }
    }

    public void next(){
        if(isFromSplash()) {
            Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
        }
    }

    private void createPermissions(){
        permissions = new ArrayList<>(REQUIRED.length + OPTIONAL.length);
        for(String identifier : REQUIRED){
            Permission permission = new Permission(
                    identifier,
                    getPermissionTitle(identifier),
                    getPermissionRationale(identifier)
            );
            permission.required = true;
            permissions.add(permission);
        }

        for(String identifier : OPTIONAL){
            Permission permission = new Permission(
                    identifier,
                    getPermissionTitle(identifier),
                    getPermissionRationale(identifier)
            );
            permission.required = isPermissionInIntent(identifier);
            permissions.add(permission);
        }
    }

    private void checkPermissions(){
        for(Permission permission : permissions){
            permission.granted = isPermissionGranted(permission.identifier);
        }
        Collections.sort(permissions, comparator);
        setNextButtonVisibility();
        adapter.notifyDataSetChanged();
    }

    private void extractPermissionInIntent(){
        Intent intent = getIntent();
        if(intent.hasExtra(IntentKey.REQUIRED_PERMISSION)){
            requiredPermission = intent.getStringExtra(IntentKey.REQUIRED_PERMISSION);
        }
    }

    private void setupNextButton(){
        nextButton = findViewById(R.id.btn_next);
        nextButton.setOnClickListener(this);
    }

    private void setNextButtonVisibility(){
        boolean granted = true;
        for(Permission permission : permissions){
            if(permission.required) {
                granted = granted && permission.granted;
            }
        }

        nextButton.setVisibility(
                granted ? View.VISIBLE : View.GONE
        );
    }

    private boolean isFromSplash(){
        return getIntent().getBooleanExtra(IntentKey.FROM_SPLASH, false);
    }

    private boolean isPermissionInIntent(String identifier){
        return requiredPermission != null && requiredPermission.equals(identifier);
    }

    private Comparator<Permission> comparator = new Comparator<Permission>() {
        @Override
        public int compare(Permission lhs, Permission rhs) {
            boolean grantedEqual = lhs.granted == rhs.granted;
            boolean requiredEqual = lhs.required == rhs.required;
            if(grantedEqual && requiredEqual) {
                return 0;
            } else if(grantedEqual == requiredEqual){
                // (granted, required) & (denied, optional)
                //or (granted, optional) & (denied, required)
                return lhs.granted ? 1 : -1;
            } else if(!requiredEqual){
                // granted is equal and required is not
                return lhs.required ? -1 : 1;
            } else {
                //required is equal and granted is not
                return lhs.granted ? 1 : -1;
            }
        }
    };

    private boolean isPermissionGranted(String identifier){
        return isPermissionGranted(this, identifier);
    }

    private static int getRequestCode(String identifier){
        if(DRAW_OVER_OTHER_APPS.equals(identifier)){
            return RequestCode.DRAW_OVER_OTHER_APPS;
        } else if(Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(identifier)){
            return RequestCode.STORAGE;
        } else {
            return 0;
        }
    }

    private static String getIdentifier(int requestCode){
        switch (requestCode){
            case RequestCode.DRAW_OVER_OTHER_APPS:
                return DRAW_OVER_OTHER_APPS;
            case RequestCode.STORAGE:
                return Manifest.permission.WRITE_EXTERNAL_STORAGE;
            default:
                return null;
        }
    }

    private static boolean isPermissionGranted(Context context, String identifier){
        if(DRAW_OVER_OTHER_APPS.equals(identifier)){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return Settings.canDrawOverlays(context);
            } else {
                return true;
            }
        } else {
            int result = ContextCompat.checkSelfPermission(context, identifier);
            return result == PackageManager.PERMISSION_GRANTED;
        }
    }

    public static boolean areRequiredPermissionGranted(Context context){
        boolean granted = true;
        for(String identifier : REQUIRED){
            granted = granted && isPermissionGranted(context, identifier);
        }
        return granted;
    }

    private static @StringRes int getPermissionTitle(String identifier){
        if(DRAW_OVER_OTHER_APPS.equals(identifier)){
            return R.string.permission_title_draw_over_other_apps;
        } else if(Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(identifier)){
            return R.string.permission_title_storage;
        } else {
            return R.string.permission_title_unknown;
        }
    }

    private static @StringRes int getPermissionRationale(String identifier){
        if(DRAW_OVER_OTHER_APPS.equals(identifier)){
            return R.string.permission_rationale_draw_over_other_apps;
        } else if(Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(identifier)){
            return R.string.permission_rationale_storage;
        } else {
            return R.string.permission_rationale_unknown;
        }
    }

    private static @StringRes int getPermissionRequestMessage(String identifier){
        if(DRAW_OVER_OTHER_APPS.equals(identifier)){
            return R.string.permission_request_message_draw_over_other_apps;
        } else if(Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(identifier)){
            return R.string.permission_request_message_storage;
        } else {
            return R.string.permission_request_message_unknown;
        }
    }

    public static class Permission{
        public String identifier;
        public @StringRes int title;
        public @StringRes int rationale;
        public boolean granted = false;
        public boolean required = false;

        public Permission(String identifier, @StringRes int title, @StringRes int rationale){
            this.identifier = identifier;
            this.title = title;
            this.rationale = rationale;
        }
    }
}
