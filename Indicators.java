String[] Colors = {"Cherry", "Cotton Candy", "Flare", "Flower", "Gold", "Grayscale", "Royal", "Sky", "Vine"};
int inAirTicks; 

void onLoad() {
    modules.registerDescription("-----bedaura progress settings----");
    modules.registerSlider(scriptName, "Rectangle Height", "px", 10, 5, 50, 1);
    modules.registerSlider(scriptName, "Rectangle Thickness", "px", 10, 1, 50, 1);
    modules.registerSlider(scriptName, "Color Mode", "", 0, Colors);  
    modules.registerDescription("-----longjump progress settings----");
    modules.registerButton("Longjump Airticks Indicator", true);
    modules.registerSlider(scriptName, "Longjump Rectangle Height", "px", 10, 5, 50, 1);
    modules.registerSlider(scriptName, "Longjump Rectangle Thickness", "px", 10, 1, 50, 1);
    modules.registerSlider(scriptName, "Longjump Color Mode", "", 0, Colors); 
}

void onPreMotion(PlayerState state) {
    inAirTicks = state.onGround ? 0 : inAirTicks + 1;
}

void onRenderTick(float partialTicks) {
    if (modules.isEnabled("BedAura")) {
        renderBedAuraProgress();
    }
    if (modules.isEnabled("Long Jump") && modules.getButton(scriptName, "Longjump Airticks Indicator")) {
        renderLongJumpProgress();
    }
}

void renderBedAuraProgress() {
    float[] progress = modules.getBedAuraProgress();
    float breakProgress = progress[0];
    if (breakProgress <= 0) return;

    float startX = 420;
    float y = 230;
    float width = breakProgress * 100;
    float height = (float) modules.getSlider(scriptName, "Rectangle Height");
    float fullWidth = 125;
    float outlineThickness = (float) modules.getSlider(scriptName, "Rectangle Thickness");

    Color[] gradient = getGradientColors((int) modules.getSlider(scriptName, "Color Mode"));
    renderProgressBar(startX, y, width, height, fullWidth, outlineThickness, gradient[0], gradient[1]);
}

void renderLongJumpProgress() {
    float startX = 420; 
    float y = 230; 
    float width = Math.min(inAirTicks * 3, 100);
    float height = (float) modules.getSlider(scriptName, "Longjump Rectangle Height");
    float fullWidth = 100;
    float outlineThickness = (float) modules.getSlider(scriptName, "Longjump Rectangle Thickness");

    Color[] gradient = getGradientColors((int) modules.getSlider(scriptName, "Longjump Color Mode"));
    renderProgressBar(startX, y, width, height, fullWidth, outlineThickness, gradient[0], gradient[1]);
}

void renderProgressBar(float startX, float y, float width, float height, float fullWidth, float outlineThickness, Color startColor, Color endColor) {
    render.rect(startX - outlineThickness, y - outlineThickness, startX + fullWidth + outlineThickness, y, Color.BLACK.getRGB());
    render.rect(startX - outlineThickness, y + height, startX + fullWidth + outlineThickness, y + height + outlineThickness, Color.BLACK.getRGB());
    render.rect(startX - outlineThickness, y, startX, y + height, Color.BLACK.getRGB());
    render.rect(startX + fullWidth, y, startX + fullWidth + outlineThickness, y + height, Color.BLACK.getRGB());


    for (int i = 0; i < width; i++) {
        float ratio = (float) i / width;
        int red = (int) (startColor.getRed() * (1 - ratio) + endColor.getRed() * ratio);
        int green = (int) (startColor.getGreen() * (1 - ratio) + endColor.getGreen() * ratio);
        int blue = (int) (startColor.getBlue() * (1 - ratio) + endColor.getBlue() * ratio);

        render.rect(startX + i, y, startX + i + 1, y + height, new Color(red, green, blue).getRGB());
    }
}

Color[] getGradientColors(int mode) {
    switch (mode) {
        case 0: return new Color[]{new Color(255, 200, 200), new Color(243, 58, 106)};
        case 1: return new Color[]{new Color(99, 249, 255), new Color(255, 104, 204)}; 
        case 2: return new Color[]{new Color(231, 39, 24), new Color(245, 173, 49)}; 
        case 3: return new Color[]{new Color(215, 166, 231), new Color(211, 90, 232)};
        case 4: return new Color[]{new Color(255, 215, 0), new Color(240, 159, 0)}; 
        case 5: return new Color[]{new Color(240, 240, 240), new Color(110, 110, 110)}; 
        case 6: return new Color[]{new Color(125, 204, 241), new Color(30, 71, 170)}; 
        case 7: return new Color[]{new Color(160, 230, 225), new Color(15, 190, 220)};
        case 8: return new Color[]{new Color(17, 192, 45), new Color(201, 234, 198)}; 
        default: return new Color[]{new Color(255, 255, 255), new Color(0, 0, 0)};
    }
}
