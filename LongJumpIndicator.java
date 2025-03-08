int inAirTicks;

void onLoad() {
    modules.registerSlider("Rectangle Height", "px", 10, 5, 50, 1);
    modules.registerSlider("Rect Thickness", "px", 10, 1, 50, 1);
    

    modules.registerSlider("Rect Start Red", "R", 0, 0, 255, 1);
    modules.registerSlider("Rect Start Green", "G", 120, 0, 255, 1);
    modules.registerSlider("Rect Start Blue", "B", 255, 0, 255, 1);
    

    modules.registerSlider("Rect End Red", "R", 255, 0, 255, 1);
    modules.registerSlider("Rect End Green", "G", 0, 0, 255, 1);
    modules.registerSlider("Rect End Blue", "B", 0, 0, 255, 1);
    
   
    modules.registerSlider("Outline Red", "R", 255, 0, 255, 1);
    modules.registerSlider("Outline Green", "G", 255, 0, 255, 1);
    modules.registerSlider("Outline Blue", "B", 255, 0, 255, 1);
    

    modules.registerButton("Enable Extra Outline", true);
}

void onPreMotion(PlayerState state) {
    inAirTicks = state.onGround ? 0 : inAirTicks + 1;
}

void onRenderTick(float partialTicks) {
    if (!modules.isEnabled("Long Jump")) return;
    
    float startX = 430;
    float y = 230;
    float width = Math.min(inAirTicks * 3, 100);
    float height = (int) modules.getSlider(scriptName, "Rectangle Height");
    float fullWidth = 100;
    float outlineThickness = (int) modules.getSlider(scriptName, "Rect Thickness");

 
    int outlineRed = (int) modules.getSlider(scriptName, "Outline Red");
    int outlineGreen = (int) modules.getSlider(scriptName, "Outline Green");
    int outlineBlue = (int) modules.getSlider(scriptName, "Outline Blue");
    Color outlineColor = new Color(outlineRed, outlineGreen, outlineBlue);
    
   
    render.rect(startX - outlineThickness, y - outlineThickness, startX + fullWidth + outlineThickness, y, outlineColor.getRGB());
    render.rect(startX - outlineThickness, y + height, startX + fullWidth + outlineThickness, y + height + outlineThickness, outlineColor.getRGB());
    render.rect(startX - outlineThickness, y, startX, y + height, outlineColor.getRGB());
    render.rect(startX + fullWidth, y, startX + fullWidth + outlineThickness, y + height, outlineColor.getRGB());

   
    int startRed = (int) modules.getSlider(scriptName, "Rect Start Red");
    int startGreen = (int) modules.getSlider(scriptName, "Rect Start Green");
    int startBlue = (int) modules.getSlider(scriptName, "Rect Start Blue");
    
 
    int endRed = (int) modules.getSlider(scriptName, "Rect End Red");
    int endGreen = (int) modules.getSlider(scriptName, "Rect End Green");
    int endBlue = (int) modules.getSlider(scriptName, "Rect End Blue");

    for (int i = 0; i < width; i++) {
        float ratio = (float) i / width; 
        int red = (int) (startRed * (1 - ratio) + endRed * ratio);
        int green = (int) (startGreen * (1 - ratio) + endGreen * ratio);
        int blue = (int) (startBlue * (1 - ratio) + endBlue * ratio);
        
        render.rect(startX + i, y, startX + i + 1, y + height, new Color(red, green, blue).getRGB());
    }
    
 
    if (modules.getButton(scriptName, "Enable Extra Outline")) {
        render.rect(startX - outlineThickness, y - outlineThickness, startX + fullWidth + outlineThickness, y, outlineColor.getRGB());
        render.rect(startX - outlineThickness, y + height, startX + fullWidth + outlineThickness, y + height + outlineThickness, outlineColor.getRGB());
        render.rect(startX - outlineThickness, y, startX, y + height, outlineColor.getRGB());
        render.rect(startX + fullWidth, y, startX + fullWidth + outlineThickness, y + height, outlineColor.getRGB());
    }
}
