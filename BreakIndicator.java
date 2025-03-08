void onLoad() {
    modules.registerSlider("Rectangle Start Red", "R", 0, 0, 255, 1);
    modules.registerSlider("Rectangle Start Green", "G", 120, 0, 255, 1);
    modules.registerSlider("Rectangle Start Blue", "B", 255, 0, 255, 1);

    modules.registerSlider("Rectangle End Red", "R", 255, 0, 255, 1);
    modules.registerSlider("Rectangle End Green", "G", 0, 0, 255, 1);
    modules.registerSlider("Rectangle End Blue", "B", 0, 0, 255, 1);

    modules.registerSlider("Rectangle Height", "px", 10, 5, 50, 1);
    modules.registerSlider("Rectangle Thickness", "px", 10, 1, 50, 1);

    modules.registerSlider("Outline Red", "R", 255, 0, 255, 1);
    modules.registerSlider("Outline Green", "G", 255, 0, 255, 1);
    modules.registerSlider("Outline Blue", "B", 255, 0, 255, 1);
}

void onRenderTick(float partialTicks) {
    if (!modules.isEnabled("BedAura")) return; 

    float[] progress = modules.getBedAuraProgress();
    float breakProgress = progress[0]; 

    if (breakProgress <= 0) return; 

    float startX = 420;  
    float y = 230;
    float width = breakProgress * 100;  
    float height = (int) modules.getSlider(scriptName, "Rectangle Height");  
    float fullWidth = 125;  
    float outlineThickness = (int) modules.getSlider(scriptName, "Rectangle Thickness");  

  
    int startRed = (int) modules.getSlider(scriptName, "Rectangle Start Red");
    int startGreen = (int) modules.getSlider(scriptName, "Rectangle Start Green");
    int startBlue = (int) modules.getSlider(scriptName, "Rectangle Start Blue");

   
    int endRed = (int) modules.getSlider(scriptName, "Rectangle End Red");
    int endGreen = (int) modules.getSlider(scriptName, "Rectangle End Green");
    int endBlue = (int) modules.getSlider(scriptName, "Rectangle End Blue");

 
    int outlineRed = (int) modules.getSlider(scriptName, "Outline Red");
    int outlineGreen = (int) modules.getSlider(scriptName, "Outline Green");
    int outlineBlue = (int) modules.getSlider(scriptName, "Outline Blue");
    Color outlineColor = new Color(outlineRed, outlineGreen, outlineBlue);

  
    render.rect(startX - outlineThickness, y - outlineThickness, startX + fullWidth + outlineThickness, y, outlineColor.getRGB());  
    render.rect(startX - outlineThickness, y + height, startX + fullWidth + outlineThickness, y + height + outlineThickness, outlineColor.getRGB()); 
    render.rect(startX - outlineThickness, y, startX, y + height, outlineColor.getRGB());  
    render.rect(startX + fullWidth, y, startX + fullWidth + outlineThickness, y + height, outlineColor.getRGB());  


    for (int i = 0; i < width; i++) {
        float ratio = (float) i / width;
        int red = (int) (startRed * (1 - ratio) + endRed * ratio);
        int green = (int) (startGreen * (1 - ratio) + endGreen * ratio);
        int blue = (int) (startBlue * (1 - ratio) + endBlue * ratio);

        render.rect(startX + i, y, startX + i + 1, y + height, new Color(red, green, blue).getRGB());
    }
}
