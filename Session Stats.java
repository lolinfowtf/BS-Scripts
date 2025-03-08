
double eventTimersX, eventTimersY, sessionInfoX, sessionInfoY;
double mainBgHeight, playerHeadWidth, playerHeadHeight, sessionBoxHeight;
int mainBgAlpha, topBarAlpha, topBarHeight;
String playerName, serverIP, playerUUID;
Image playerHeadImage;

long joinTime; 


double textOffsetX, textOffsetY, textDistance; 
double rectOffsetX, rectOffsetY;
double playerHeadOffsetX, playerHeadOffsetY;
double topBarOffsetX, topBarOffsetY;

void onLoad() {

    modules.registerSlider("Session Info X", "px", 250, -500, 870, 1);
    modules.registerSlider("Session Info Y", "px", 220, -500, 500, 1);  
    modules.registerSlider("Main Background Alpha", "", 120, 0, 255, 1);
    modules.registerSlider("Top Bar Alpha", "", 200, 0, 255, 1);
    modules.registerSlider("Top Bar Height", "px", 5, 1, 15, 1);
    modules.registerSlider("Main Background Height", "px", 120, 50, 500, 1); 
    modules.registerSlider("Player Head Width", "px", 24, 10, 50, 1); 
    modules.registerSlider("Player Head Height", "px", 24, 10, 50, 1);
    modules.registerSlider("Session Box Height", "px", 120, 50, 500, 1);  


    modules.registerSlider("Text X Offset", "px", 40, -50, 50, 1); 
    modules.registerSlider("Text Y Offset", "px", 10, -50, 50, 1);
    modules.registerSlider("Text Distance", "px", 15, -20, 30, 1);
    modules.registerSlider("Rectangle X Offset", "px", 0, -50, 50, 1);
    modules.registerSlider("Rectangle Y Offset", "px", 0, -50, 50, 1);
    modules.registerSlider("Player Head X Offset", "px", 5, -50, 50, 1);
    modules.registerSlider("Player Head Y Offset", "px", 0, -50, 50, 1);
    modules.registerSlider("Top Bar X Offset", "px", 0, -50, 50, 1);
    modules.registerSlider("Top Bar Y Offset", "px", 0, -50, 50, 1);


    Entity player = client.getPlayer(); 
    playerName = player.getName(); 
    playerUUID = player.getUUID();


    serverIP = client.getServerIP();
    playerHeadImage = new Image("https://crafatar.com/avatars/" + playerUUID, true);


    joinTime = System.currentTimeMillis();
}

void onPreUpdate() {

    sessionInfoX = modules.getSlider(scriptName, "Session Info X");
    sessionInfoY = modules.getSlider(scriptName, "Session Info Y");
    mainBgAlpha = (int) modules.getSlider(scriptName, "Main Background Alpha");
    topBarAlpha = (int) modules.getSlider(scriptName, "Top Bar Alpha");
    topBarHeight = (int) modules.getSlider(scriptName, "Top Bar Height");
    mainBgHeight = modules.getSlider(scriptName, "Main Background Height");
    playerHeadWidth = modules.getSlider(scriptName, "Player Head Width");
    playerHeadHeight = modules.getSlider(scriptName, "Player Head Height");
    sessionBoxHeight = modules.getSlider(scriptName, "Session Box Height");

    textOffsetX = modules.getSlider(scriptName, "Text X Offset");
    textOffsetY = modules.getSlider(scriptName, "Text Y Offset");
    textDistance = modules.getSlider(scriptName, "Text Distance");
    rectOffsetX = modules.getSlider(scriptName, "Rectangle X Offset");
    rectOffsetY = modules.getSlider(scriptName, "Rectangle Y Offset");
    playerHeadOffsetX = modules.getSlider(scriptName, "Player Head X Offset");
    playerHeadOffsetY = modules.getSlider(scriptName, "Player Head Y Offset");
    topBarOffsetX = modules.getSlider(scriptName, "Top Bar X Offset");
    topBarOffsetY = modules.getSlider(scriptName, "Top Bar Y Offset");
}

void onRenderTick(float partialTicks) {
  
    float x = (float) sessionInfoX + (float) rectOffsetX;
    float y = (float) sessionInfoY + (float) rectOffsetY;

    float boxWidth = 180f;
    float boxHeight = (float) sessionBoxHeight;
    float textOffsetXFinal = (float) textOffsetX;
    float textOffsetYFinal = (float) textOffsetY;


    render.rect(x, y, x + boxWidth, y + boxHeight, new Color(0, 0, 0, mainBgAlpha).getRGB());


    float topBarBottom = y + (float) topBarOffsetY;
    float topBarTop = y - topBarHeight + (float) topBarOffsetY;
    render.rect(x + (float) topBarOffsetX, topBarTop, x + boxWidth, topBarBottom, new Color(50, 50, 50, topBarAlpha).getRGB());

 
    String title = "Session Information";
    int titleWidth = render.getFontWidth(title);
    float titleX = x + (boxWidth / 2.0f) - (titleWidth / 2.0f) + (float) topBarOffsetX;
    float titleY = topBarTop + (topBarHeight / 2.0f) - 4.0f + (float) topBarOffsetY;
    render.text2d(title, titleX, titleY, 1.0f, new Color(224, 224, 224).getRGB(), true);

    if (playerHeadImage != null) {
        render.image(playerHeadImage, x + (float) playerHeadOffsetX, y + (boxHeight / 2.0f) - (float) playerHeadHeight / 2.0f + (float) playerHeadOffsetY, (float) playerHeadWidth, (float) playerHeadHeight);
    }


    render.text2d("Name: " + playerName, x + textOffsetXFinal, y + textOffsetYFinal, 1.0f, Color.WHITE.getRGB(), true);
    render.text2d("Server: " + serverIP, x + textOffsetXFinal, y + textOffsetYFinal + (float) textDistance, 1.0f, Color.WHITE.getRGB(), true);

    long timePlayed = System.currentTimeMillis() - joinTime;
    String formattedTimePlayed = formatTimePlayed(timePlayed);

    render.text2d("Time Played: " + formattedTimePlayed, x + textOffsetXFinal, y + textOffsetYFinal + 2f * (float) textDistance, 1.0f, Color.WHITE.getRGB(), true);
}


String formatTimePlayed(long milliseconds) {
    long seconds = milliseconds / 1000;
    long minutes = seconds / 60;
    long hours = minutes / 60;
    seconds = seconds % 60;
    minutes = minutes % 60;

    return String.format("%02d:%02d:%02d", hours, minutes, seconds);
}


String formatTime(int seconds) {
    int minutes = seconds / 60;
    int secs = seconds % 60;
    return String.format("%02d:%02d", minutes, secs);
}
