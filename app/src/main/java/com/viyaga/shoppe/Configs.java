package com.viyaga.shoppe;

import android.app.Activity;

import com.viyaga.shoppe.helper.Characters;
import com.viyaga.shoppe.helper.ErrorMassage;
import com.viyaga.shoppe.helper.ErrorTitle;
import com.viyaga.shoppe.helper.I_Want;
import com.viyaga.shoppe.helper.ShareMessage;
import com.viyaga.shoppe.helper.TitleStyle;
import com.viyaga.shoppe.model.WebPage;

import java.util.ArrayList;
import java.util.List;

public class                                                                                                                                                                                                                                                                                                Configs {

    public static final int Horizontal = 0;
    public static final int Circular = 1;

    public static final int Bottom = 0;
    public static final int Center = 1;

    public static final boolean No = false;
    public static final boolean Yes = true;

    private Activity context;
    private int loadingStyle;
    private int color;
    private int toolBarColor;
    private int toolBarIconColor;
    private int titleColor;
    private int slidingTitleColor;
    private int slidingDescColor;
    private int slidingIcon;
    private int errorCharacter;
    private int downloadStyle;
    private String errorTitle;
    private String errorMessage;
    private String titleStyle;
    private String shareText;
    private String slidingTitle;
    private String slidingDesc;
    private boolean fullscreen;
    private String support;
    private String location;
    private String phone;
    private int splashIcon;

    public Configs(Activity context) {
        this.context = context;

        Do_you_want_to_enable_fullscreen(No);

        Splash_screen_icon(R.drawable.app_icon);

        What_is_the_color_you_want(I_Want.merun);

        What_is_the_color_you_want_for_the_toolbar(I_Want.White);

        What_is_the_color_you_want_for_the_toolbar_icons(I_Want.Black);

        What_is_the_title_style_you_want(TitleStyle.Roboto);

        What_is_the_title_color_you_want(I_Want.merun);

        Sliding_view_icon(R.mipmap.ic_launcher_round);

        Sliding_view_title("Viyaga Shopee");

        Sliding_view_title_color(I_Want.White);

        Sliding_view_description("Free Shipping Available");

        Sliding_view_description_color(I_Want.White);

        What_is_the_loading_style_you_want(Circular);

        What_is_the_download_style_you_want(Bottom);

        Choose_your_connection_error_character(R.drawable.character_nine);

        Your_connection_error_title(ErrorTitle.Style4);

        Your_connection_error_message(ErrorMassage.Style5);

        Choose_your_share_message(ShareMessage.Default);

        What_is_your_support_email_address("support@viyaga.com");

        What_is_your_business_phone_number("+91");

    }

    public List<WebPage> getWebPages() {
        List<WebPage> pages = new ArrayList<>();

        pages.add(new WebPage(0, R.drawable.ic_home, "Home", "https://myshopprime.com/viyaga.com/rxvda8z"));
        //pages.add(new WebPage(1, R.drawable.ic_explore, "About", "https://viyaga.com/about.php"));
        //pages.add(new WebPage(2, R.drawable.ic_heart, "Terms & Condition", "https://viyaga.com/terms-conditions.php"));

        return pages;
    }

    public List<WebPage> getInfoPages() {
        List<WebPage> pages = new ArrayList<>();

        pages.add(new WebPage(0, R.drawable.ic_email, "Support", support));

        return pages;
    }



    public int getSplashIcon() {
        return splashIcon;
    }

    private void Splash_screen_icon(int iconS) {
        splashIcon = iconS;
    }

    public int getLoadingStyle() {
        return loadingStyle;
    }

    public int getColor() {
        return color;
    }

    public int getToolBarColor() {
        return toolBarColor;
    }

    public int getToolBarIconColor() {
        return toolBarIconColor;
    }

    public int getTitleColor() {
        return titleColor;
    }

    public String getTitleStyle() {
        return titleStyle;
    }

    public String getShareText() {
        return shareText;
    }

    public String getSlidingTitle() {
        return slidingTitle;
    }

    public String getSlidingDesc() {
        return slidingDesc;
    }

    public int getDownloadStyle() {
        return downloadStyle;
    }

    public int getSlidingDescColor() {
        return slidingDescColor;
    }

    public int getSlidingTitleColor() {
        return slidingTitleColor;
    }

    public int getSlidingIcon() {
        return slidingIcon;
    }

    public int getErrorCharacter() {
        return errorCharacter;
    }

    public String getErrorTitle() {
        return errorTitle;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    private void What_is_the_color_you_want(int color) {
        this.color = color;
    }

    private void What_is_the_color_you_want_for_the_toolbar(int color) {
        this.toolBarColor = color;
    }

    private void What_is_the_color_you_want_for_the_toolbar_icons(int color) {
        this.toolBarIconColor = color;
    }

    private void What_is_the_title_style_you_want(String style) {
        this.titleStyle = style;
    }

    private void What_is_the_title_color_you_want(int color) {
        this.titleColor = color;
    }

    private void Choose_your_share_message(String text) {
        this.shareText = text;
    }

    private void Sliding_view_title(String text) {
        this.slidingTitle = text;
    }

    private void Sliding_view_title_color(int color) {
        this.slidingTitleColor = color;
    }

    private void Sliding_view_description(String text) {
        this.slidingDesc = text;
    }

    private void Sliding_view_description_color(int color) {
        this.slidingDescColor = color;
    }

    private void Sliding_view_icon(int icon) {
        this.slidingIcon = icon;
    }

    private void What_is_the_loading_style_you_want(int style) {
        loadingStyle = style;
    }

    private void Choose_your_connection_error_character(int character) {
        errorCharacter = character;
    }

    private void Your_connection_error_message(String message) {
        errorMessage = message;
    }

    private void Your_connection_error_title(String title) {
        errorTitle = title;
    }

    private void Do_you_want_to_enable_fullscreen(boolean answer) {
        fullscreen = answer;
    }

    private void What_is_your_support_email_address(String supportEmail) {
        this.support = supportEmail;
    }

    private void What_is_your_location_address(String location) {
        this.location = location;
    }

    private void What_is_your_business_phone_number(String phone) {
        this.phone = phone;
    }

    private void What_is_the_download_style_you_want(int s) {
        downloadStyle = s;
    }
}
