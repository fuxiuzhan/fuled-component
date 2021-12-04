package com.fxz.component.fuled.banner;


import com.nepxion.banner.Description;
import com.nepxion.banner.DescriptionBanner;
import com.nepxion.banner.LogoBanner;
import com.taobao.text.Color;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fxz
 */
public class BannerApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    public BannerApplicationContextInitializer() {
    }

    public void initialize(ConfigurableApplicationContext applicationContext) {
        LogoBanner logoBanner = new LogoBanner(BannerApplicationContextInitializer.class, "/logo.txt", "Welcome to FuledFrameWrok", 5, 5, new Color[]{Color.red, Color.green, Color.cyan, Color.blue, Color.yellow}, true);
        this.show(logoBanner);
        System.setProperty("nepxion.banner.shown", "false");
    }

    private void show(LogoBanner logoBanner) {
        String bannerShown = System.getProperty("nepxion.banner.shown", "true");
        if (Boolean.valueOf(bannerShown)) {
            System.out.println("");
            String bannerShownAnsiMode = System.getProperty("nepxion.banner.shown.ansi.mode", "false");
            if (Boolean.valueOf(bannerShownAnsiMode)) {
                System.out.println(logoBanner.getBanner());
            } else {
                System.out.println(logoBanner.getPlainBanner());
            }

            String serverType = StringUtils.capitalize(System.getProperty("spring.application.type"));
            List<Description> descriptions = new ArrayList();
            descriptions.add(new Description("Server:", "FuledFrameWork " + (serverType == null ? "" : "Application"), 0, 1));
            descriptions.add(new Description("Version:", "1.0.0.WaterDrop", 0, 1));
            descriptions.add(new Description("GitHub:", "http://192.168.10.201:8082/fuled/fuled-framework.git", 0, 1));
            DescriptionBanner descriptionBanner = new DescriptionBanner();
            System.out.println(descriptionBanner.getBanner(descriptions));
        }
    }
}
