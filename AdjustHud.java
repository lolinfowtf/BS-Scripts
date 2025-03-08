
String text = "A d j u s t";

void onLoad() {
    modules.registerSlider("Text Color - Red", "", 255, 0, 255, 1);
    modules.registerSlider("Text Color - Green", "", 0, 0, 255, 1);
    modules.registerSlider("Text Color - Blue", "", 0, 0, 255, 1);
}

int textColor;

void updateSliders() {
    int colorRed = (int) modules.getSlider(scriptName, "Text Color - Red");
    int colorGreen = (int) modules.getSlider(scriptName, "Text Color - Green");
    int colorBlue = (int) modules.getSlider(scriptName, "Text Color - Blue");

    textColor = new Color(colorRed, colorGreen, colorBlue).getRGB();
}
void onPreUpdate() {

}

void onRenderTick(float partialTicks) {
    Entity plr = client.getPlayer();
    int white = -1;
    int red = (int) modules.getSlider(scriptName, "Red");
    int green = (int) modules.getSlider(scriptName, "Green");
    int blue = (int) modules.getSlider(scriptName, "Blue");
    int clr = new Color(red, green, blue).getRGB();
 
    double bps = plr.getBPS();     

    int[] displaySize = client.getDisplaySize();
    
    float textHeight = render.getFontHeight();

    updateSliders();

    // top left alignment
    float x = 2;
    float y = 2;

    String bracketOpen = " [";
    String bracketClose = "]";
    int fps = client.getFPS();
    String fpsText = fps + " FPS";
    String bpsText = bps + " BPS";
    
    float currentX = x;

    int bracketColor = new Color(200, 200, 200).getRGB();
    int fpsColor = new Color(255, 255, 255).getRGB();
    int remainingColor = new Color(255, 255, 255).getRGB();

    char firstChar = text.charAt(0);
    render.text2d(String.valueOf(firstChar), currentX, y, 1.2f, textColor, true);
    currentX += render.getFontWidth(String.valueOf(firstChar));
   

    for (int i = 1; i < text.length(); i++) {
        char c = text.charAt(i);
        render.text2d(String.valueOf(c), currentX, y, 1.2f, remainingColor, true);
        currentX += render.getFontWidth(String.valueOf(c));
    }



   
    }




int clamp(int val, int min, int max) {
    if (val < min) return min;
    if (val > max) return max;
    return val;
}
