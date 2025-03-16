String[] watermarks = {"Raven BS", "Custom Profile 1", "Custom Profile 2", "Custom Profile 3", "Custom Profile 4" ,"Custom Profile 5"};
String[] release = {"Release", "Development Build", "Beta"};
String[] customUsernames = {"Username"};

float barProgress = 0;
float slowSpeed = 2;
int moveTimer = 0;

void onLoad() {
    modules.registerSlider("Watermark", "", 0, 0, watermarks.length - 1, 1);
    modules.registerSlider("Release", "", 0, 0, release.length - 1, 1);
    modules.registerSlider("Custom Name", "", 0, 0, customUsernames.length - 1, 1);
    modules.registerSlider("Background Alpha", "", 120, 0, 255, 1);
    modules.registerSlider("Bar Alpha", "", 180, 0, 255, 1);
    modules.registerSlider("Bar Start Red", "", 0, 0, 255, 1);
    modules.registerSlider("Bar Start Green", "", 120, 0, 255, 1);
    modules.registerSlider("Bar Start Blue", "", 255, 0, 255, 1);
    modules.registerSlider("Bar End Red", "", 255, 0, 255, 1);
    modules.registerSlider("Bar End Green", "", 0, 0, 255, 1);
    modules.registerSlider("Bar End Blue", "", 120, 0, 255, 1);
    modules.registerSlider("Watermark X", "", 5, 0, 500, 1);
    modules.registerSlider("Watermark Y", "", 5, 0, 500, 1);
}

void onRenderTick(float partialTicks) {
    Entity plr = client.getPlayer();
    if (plr == null) return;

    String watermark = watermarks[(int) modules.getSlider(scriptName, "Watermark")];
    String selectedRelease = release[(int) modules.getSlider(scriptName, "Release")];
    String customName = customUsernames[(int) modules.getSlider(scriptName, "Custom Name")];
    String serverIP = client.getServerIP();
    int fps = client.getFPS();

    int ping = -1;
    for (NetworkPlayer np : world.getNetworkPlayers()) {
        if (np.getName().equals(plr.getName())) {
            ping = np.getPing();
            break;
        }
    }

    long currentTime = client.time();
    int hours = (int) ((currentTime / (1000 * 60 * 60)) % 24);
    int minutes = (int) ((currentTime / (1000 * 60)) % 60);
    String formattedTime = String.format("%02d:%02d", hours, minutes);

    String text = watermark + " (" + formattedTime + ") - " + selectedRelease + " - Update 12";

    float textWidth = render.getFontWidth(text);
    float textHeight = render.getFontHeight();
    float padding = 4;

    float posX = (float) modules.getSlider(scriptName, "Watermark X");
    float posY = (float) modules.getSlider(scriptName, "Watermark Y");

    float boxWidth = textWidth + padding * 2;
    float boxHeight = textHeight + padding * 2;

    int bgAlpha = (int) modules.getSlider(scriptName, "Background Alpha");
    render.rect(posX, posY, posX + boxWidth, posY + boxHeight, new Color(30, 30, 30, bgAlpha).getRGB());
    render.text2d(text, posX + padding, posY + padding, 1.0f, new Color(255, 255, 255).getRGB(), true);

    float totalDistance = (boxWidth * 2) + (boxHeight * 2);
    int barAlpha = (int) modules.getSlider(scriptName, "Bar Alpha");

    int startRed = (int) modules.getSlider(scriptName, "Bar Start Red");
    int startGreen = (int) modules.getSlider(scriptName, "Bar Start Green");
    int startBlue = (int) modules.getSlider(scriptName, "Bar Start Blue");

    int endRed = (int) modules.getSlider(scriptName, "Bar End Red");
    int endGreen = (int) modules.getSlider(scriptName, "Bar End Green");
    int endBlue = (int) modules.getSlider(scriptName, "Bar End Blue");

    Color startColor = new Color(startRed, startGreen, startBlue, barAlpha);
    Color endColor = new Color(endRed, endGreen, endBlue, barAlpha);

    if (moveTimer++ > 1) {
        moveTimer = 0;
        barProgress += slowSpeed;
        if (barProgress >= totalDistance) {
            barProgress = 0;
        }
    }

    float progress = barProgress / totalDistance;
    int blendedColor = blendColors(startColor.getRGB(), endColor.getRGB(), progress);

    float shrinkAmountTop = 0;

    if (barProgress <= boxWidth) {
        render.rect(posX + shrinkAmountTop, posY, posX + barProgress, posY + 2, blendedColor);
    } else if (barProgress <= boxWidth + boxHeight) {
        render.rect(posX + boxWidth - 2, posY, posX + boxWidth, posY + (barProgress - boxWidth), blendedColor);
    } else if (barProgress <= (2 * boxWidth) + boxHeight) {
        render.rect(posX + (boxWidth - (barProgress - (boxWidth + boxHeight))), posY + boxHeight - 2, posX + boxWidth, posY + boxHeight, blendedColor);
    } else {
        render.rect(posX, posY + (boxHeight - (barProgress - ((2 * boxWidth) + boxHeight))), posX + 2, posY + boxHeight, blendedColor);
    }
}

int blendColors(int color1, int color2, float ratio) {
    int r1 = (color1 >> 16) & 0xFF;
    int g1 = (color1 >> 8) & 0xFF;
    int b1 = color1 & 0xFF;
    int r2 = (color2 >> 16) & 0xFF;
    int g2 = (color2 >> 8) & 0xFF;
    int b2 = color2 & 0xFF;
    int r = (int) (r1 + (r2 - r1) * ratio);
    int g = (int) (g1 + (g2 - g1) * ratio);
    int b = (int) (b1 + (b2 - b1) * ratio);
    return (0xFF << 24) | (r << 16) | (g << 8) | b;
}
