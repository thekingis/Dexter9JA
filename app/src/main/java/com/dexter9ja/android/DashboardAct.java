package com.dexter9ja.android;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.dexter9ja.android.Adapters.ViewPagerAdapter;
import com.dexter9ja.android.Utils.MenuBox;
import com.dexter9ja.android.Utils.MenuList;
import com.dexter9ja.android.Utils.SharedCompactActivity;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Objects;

public class DashboardAct extends SharedCompactActivity {

    RelativeLayout mainView;
    LinearLayout menuLayout;
    TabLayout tabLayout;
    ViewPager viewPager;
    ImageView photo;
    TextView nameTextView;
    String firstName;
    int[] tabIcons;
    int selectedIcon;
    HomeFragment homeFragment;
    CoursesFragment coursesFragment;
    ClassesFragment classesFragment;
    ShopFragment shopFragment;
    ArrayList<MenuBox> menuLists;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        selectedIcon = 0;
        firstName = sharedPrefMngr.getUserFirstName();

        mainView = findViewById(R.id.mainView);
        menuView = findViewById(R.id.menuView);
        menuLayout = findViewById(R.id.menuLayout);
        photo = findViewById(R.id.photo);
        nameTextView = findViewById(R.id.nameTextView);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        tabIcons = new int[]{
                R.drawable.ic_home,
                R.drawable.ic_courses,
                R.drawable.ic_classes,
                R.drawable.ic_shopping_cart
        };

        nameTextView.setText(firstName);
        tabLayout.setupWithViewPager(viewPager);

        photo.setOnClickListener(this::toggleMenuLayout);
        nameTextView.setOnClickListener(this::toggleMenuLayout);

        setupMenuList();
        setupViewPager();
        setupTabIcons();
        setupUI(mainView);

    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    private void setupMenuList() {
        menuLists = new ArrayList<>();
        MenuList menuList1 = new MenuList("Profile", R.drawable.ic_person_white);
        MenuList menuList2 = new MenuList("My Learning", R.drawable.ic_school_white);
        MenuList menuList3 = new MenuList("Wishlist", R.drawable.ic_list_white);
        MenuList menuList4 = new MenuList("Posts", R.drawable.ic_article_white);
        MenuList menuList5 = new MenuList("Notifications", R.drawable.ic_notifications_white);
        MenuList menuList6 = new MenuList("Messages", R.drawable.ic_mail_white);
        MenuList menuList7 = new MenuList("Account Settings", R.drawable.ic_manage_accounts_white);
        MenuList menuList8 = new MenuList("Purchase History", R.drawable.ic_wallet_white);
        MenuList menuList9 = new MenuList("Help", R.drawable.ic_help_white);
        MenuList menuList10 = new MenuList("Contact", R.drawable.ic_contact_support_white);

        ArrayList<MenuList> menuLists1 = new ArrayList<>();
        ArrayList<MenuList> menuLists2 = new ArrayList<>();
        ArrayList<MenuList> menuLists3 = new ArrayList<>();
        ArrayList<MenuList> menuLists4 = new ArrayList<>();
        ArrayList<MenuList> menuLists5 = new ArrayList<>();

        menuLists1.add(menuList1);
        menuLists2.add(menuList2);
        menuLists2.add(menuList3);
        menuLists2.add(menuList4);
        menuLists3.add(menuList5);
        menuLists3.add(menuList6);
        menuLists4.add(menuList7);
        menuLists4.add(menuList8);
        menuLists5.add(menuList9);
        menuLists5.add(menuList10);

        MenuBox menuBox1 = new MenuBox("MAIN", menuLists1);
        MenuBox menuBox2 = new MenuBox("LISTS", menuLists2);
        MenuBox menuBox3 = new MenuBox("GENERAL", menuLists3);
        MenuBox menuBox4 = new MenuBox("MAINTENANCE", menuLists4);
        MenuBox menuBox5 = new MenuBox("ANALYTICS", menuLists5);

        menuLists.add(menuBox1);
        menuLists.add(menuBox2);
        menuLists.add(menuBox3);
        menuLists.add(menuBox4);
        menuLists.add(menuBox5);

        for(int i = 0; i < menuLists.size(); i++){
            MenuBox menuBox = menuLists.get(i);
            String menuBoxTitle = menuBox.title;
            ArrayList<MenuList> menuBoxList = menuBox.menuList;
            LinearLayout menuBoxLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.menu_list_box, null);
            TextView menuTitleView = menuBoxLayout.findViewById(R.id.menuTitleView);
            LinearLayout menuListView = menuBoxLayout.findViewById(R.id.menuListView);
            menuTitleView.setText(menuBoxTitle);
            for (int x = 0; x < menuBoxList.size(); x++){
                MenuList menuList = menuBoxList.get(x);
                String menuTitle = menuList.title;
                int menuIcon = menuList.icon;
                LinearLayout menuListLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.menu_list, null);
                TextView menuTextView = menuListLayout.findViewById(R.id.menuTextView);
                menuTextView.setText(menuTitle);
                menuTextView.setCompoundDrawablesWithIntrinsicBounds(menuIcon, 0, 0, 0);
                menuListView.addView(menuListLayout);
            }
            menuLayout.addView(menuBoxLayout);
        }
        LinearLayout logoutLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.bottom_menu_list, null);
        TextView logoutLayoutTextView = logoutLayout.findViewById(R.id.menuTextView);
        logoutLayoutTextView.setText("Logout");
        logoutLayoutTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_power_settings_white, 0, 0, 0);
        menuLayout.addView(logoutLayout);
    }

    private void setupViewPager() {
        viewPager.setOffscreenPageLimit(5);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        homeFragment = new HomeFragment(context);
        coursesFragment = new CoursesFragment(context);
        classesFragment = new ClassesFragment(context);
        shopFragment = new ShopFragment(context);
        adapter.addFragment(homeFragment, "Home");
        adapter.addFragment(coursesFragment, "Courses");
        adapter.addFragment(classesFragment, "Classes");
        adapter.addFragment(shopFragment, "Shop");
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        for (int x = 0; x < tabIcons.length; x++) {
            Objects.requireNonNull(tabLayout.getTabAt(x)).setIcon(tabIcons[x]);
        }
        TabLayout.Tab tab = tabLayout.getTabAt(selectedIcon);
        int color = ContextCompat.getColor(context, R.color.primary);
        if (tab != null)
            Objects.requireNonNull(tab.getIcon()).setColorFilter(color, PorterDuff.Mode.SRC_IN);
        changeIconColor();
    }

    private void changeIconColor() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TabLayout.Tab exTab = tabLayout.getTabAt(selectedIcon);
                int color = ContextCompat.getColor(context, R.color.primary);
                int exColor = ContextCompat.getColor(context, R.color.gray);
                if (tab != null)
                    Objects.requireNonNull(tab.getIcon()).setColorFilter(color, PorterDuff.Mode.SRC_IN);
                if (exTab != null)
                    Objects.requireNonNull(exTab.getIcon()).setColorFilter(exColor, PorterDuff.Mode.SRC_IN);
                selectedIcon = tabLayout.getSelectedTabPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if(menuIsVisible) {
            toggleMenuLayout(null);
            return;
        }
        if(canCloseBlackFade && !(blackFade == null)){
            blackFade.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
    }

}