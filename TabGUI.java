int selectedCategory = 0;
int selectedModule = 0;
boolean categoryOpen = false;
boolean keyReleased = true;

HashMap<String, List<String>> categorizedModules = new HashMap<>();

void onLoad() {
    categorizedModules.put("Combat", Arrays.asList("KillAura", "AutoClicker", "AntiKnockback", "AimAssist", "BurstClicker", "ClickAssist", "HitBox", "Jump Reset", "Reach", "Reduce", "RodAimbot", "TPAura", "Velocity", "WTap"));
    categorizedModules.put("Player", Arrays.asList("AntiAFK", "Scaffold", "NoFall", "FastPlace", "AutoTool"));
    categorizedModules.put("Movement", Arrays.asList("Speed", "Long Jump", "Fly", "BHop", "Sprint"));
    categorizedModules.put("Render", Arrays.asList("HUD", "Tracers", "Nametags", "Xray", "Chams"));
    categorizedModules.put("Exploits", Arrays.asList("Chat Bypass", "View Packets", "Anticheat"));
    
    modules.registerSlider("TabGUI X Offset", "px", 50, 0, 500, 1);
    modules.registerSlider("TabGUI Y Offset", "px", 50, 0, 500, 1);
    modules.registerSlider("TabGUI Width", "px", 100, 50, 300, 1);
    modules.registerSlider("TabGUI Height", "px", 14, 10, 30, 1);
    modules.registerSlider("TabGUI Background Alpha", "A", 180, 0, 255, 1);
    

    modules.registerSlider("TabGUI Text Offset (X)", "px", 5, -20, 50, 1);
    modules.registerSlider("TabGUI Text Offset (Y)", "px", 4, -20, 50, 1);

    modules.registerSlider("Category Gradient Start Red", "R", 0, 0, 255, 1);
    modules.registerSlider("Category Gradient Start Green", "G", 120, 0, 255, 1);
    modules.registerSlider("Category Gradient Start Blue", "B", 255, 0, 255, 1);

    modules.registerSlider("Category Gradient End Red", "R", 0, 0, 255, 1);
    modules.registerSlider("Category Gradient End Green", "G", 255, 0, 255, 1);
    modules.registerSlider("Category Gradient End Blue", "B", 255, 0, 255, 1);
    
    modules.registerSlider("TabGUI Separator Red", "R", 255, 0, 255, 1);
    modules.registerSlider("TabGUI Separator Green", "G", 255, 0, 255, 1);
    modules.registerSlider("TabGUI Separator Blue", "B", 255, 0, 255, 1);
    modules.registerSlider("TabGUI Separator Alpha", "A", 180, 0, 255, 1);
    
    modules.registerSlider("TabGUI Separator Offset", "px", 1, 0, 10, 1);
    modules.registerSlider("TabGUI Separator Height", "px", 2, 1, 5, 1);
}

void onRenderTick(float partialTicks) {
    handleInput();

    float x = (int) modules.getSlider(scriptName, "TabGUI X Offset");
    float y = (int) modules.getSlider(scriptName, "TabGUI Y Offset");
    float width = (int) modules.getSlider(scriptName, "TabGUI Width");
    float height = (int) modules.getSlider(scriptName, "TabGUI Height");
    int alpha = (int) modules.getSlider(scriptName, "TabGUI Background Alpha");

    int separatorOffset = (int) modules.getSlider(scriptName, "TabGUI Separator Offset");
    int separatorHeight = (int) modules.getSlider(scriptName, "TabGUI Separator Height");

    int separatorRed = (int) modules.getSlider(scriptName, "TabGUI Separator Red");
    int separatorGreen = (int) modules.getSlider(scriptName, "TabGUI Separator Green");
    int separatorBlue = (int) modules.getSlider(scriptName, "TabGUI Separator Blue");
    int separatorAlpha = (int) modules.getSlider(scriptName, "TabGUI Separator Alpha");

    Color separatorColor = new Color(separatorRed, separatorGreen, separatorBlue, separatorAlpha);

    float textXOffset = (int) modules.getSlider(scriptName, "TabGUI Text Offset (X)");
    float textYOffset = (int) modules.getSlider(scriptName, "TabGUI Text Offset (Y)");

    int textColor = new Color(255, 255, 255, 200).getRGB();

    List<String> categories = new ArrayList<>(categorizedModules.keySet());


    for (int i = 0; i < categories.size(); i++) {
        String category = categories.get(i);
        boolean isSelected = (i == selectedCategory);

        if (isSelected) {
            drawGradientRect(x, y + (i * height), x + width, y + (i * height) + height,
                getGradientColor("Category Gradient Start"), 
                getGradientColor("Category Gradient End"));
        } else {
            render.rect(x, y + (i * height), x + width, y + (i * height) + height, new Color(30, 30, 30, alpha).getRGB());
        }

        if (i < categories.size() - 1) {
            render.rect(x + separatorOffset, y + ((i + 1) * height) - (separatorHeight / 2), 
                        x + width - separatorOffset, y + ((i + 1) * height) + (separatorHeight / 2), 
                        separatorColor.getRGB());
        }

        render.text2d(category, x + textXOffset, y + (i * height) + textYOffset, 1.0f, textColor, true);
    }

    if (categoryOpen) {
        List<String> modulesInCategory = categorizedModules.get(categories.get(selectedCategory));

        float moduleX = x + width + 5;  
        float moduleWidth = width;  
        float moduleY = y;
        int moduleAlpha = alpha - 50;  
        for (int i = 0; i < modulesInCategory.size(); i++) {
            String module = modulesInCategory.get(i);
            boolean isModuleSelected = (i == selectedModule);

            if (isModuleSelected) {
                drawGradientRect(moduleX, moduleY + (i * height), moduleX + moduleWidth, moduleY + (i * height) + height,
                    getGradientColor("Category Gradient Start"), 
                    getGradientColor("Category Gradient End"));
            } else {
                render.rect(moduleX, moduleY + (i * height), moduleX + moduleWidth, moduleY + (i * height) + height, 
                    new Color(30, 30, 30, moduleAlpha).getRGB());
            }

            render.text2d(module, moduleX + textXOffset, moduleY + (i * height) + textYOffset, 1.0f, textColor, true);
        }
    }
}




void handleInput() {
    boolean keyUp = keybinds.isKeyDown(200);
    boolean keyDown = keybinds.isKeyDown(208);
    boolean keyRight = keybinds.isKeyDown(205);
    boolean keyLeft = keybinds.isKeyDown(203);
    boolean keyEnter = keybinds.isKeyDown(28);

    if (keyUp || keyDown || keyRight || keyLeft || keyEnter) {
        if (keyReleased) {
            List<String> categories = new ArrayList<>(categorizedModules.keySet());
            List<String> modulesInCategory = categorizedModules.get(categories.get(selectedCategory));

            if (!categoryOpen) {
                if (keyUp) selectedCategory = (selectedCategory - 1 + categories.size()) % categories.size();
                if (keyDown) selectedCategory = (selectedCategory + 1) % categories.size();
                if (keyRight) categoryOpen = true;
            } else {
                if (keyUp) selectedModule = (selectedModule - 1 + modulesInCategory.size()) % modulesInCategory.size();
                if (keyDown) selectedModule = (selectedModule + 1) % modulesInCategory.size();
                if (keyLeft) categoryOpen = false;
                if (keyEnter) toggleModule(modulesInCategory.get(selectedModule));
            }

            keyReleased = false;
        }
    } else {
        keyReleased = true;
    }
}

void toggleModule(String moduleName) {
    if (modules.isEnabled(moduleName)) {
        modules.disable(moduleName);
    } else {
        modules.enable(moduleName);
    }
}

Color getGradientColor(String settingPrefix) {
    int red = (int) modules.getSlider(scriptName, settingPrefix + " Red");
    int green = (int) modules.getSlider(scriptName, settingPrefix + " Green");
    int blue = (int) modules.getSlider(scriptName, settingPrefix + " Blue");
    return new Color(red, green, blue);
}

void drawGradientRect(float x1, float y1, float x2, float y2, Color startColor, Color endColor) {
    render.gradientRect(x1, y1, x2, y2, startColor.getRGB(), endColor.getRGB());
}
