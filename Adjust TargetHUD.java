double targetHudX, targetHudY;
float currentHealthBarFillWidth = 1.0f;
HashMap<String, Image> playerHeads = new HashMap<>();
float lastHealthBarFillWidth = 1.0f;
int ghostBarTicks = 0;
int damageTicks = 0;
float lastTargetHealth = -1;
boolean followPlayer = false;

void onLoad() {
    modules.registerSlider("TargetHud X", "px", 250, 0, 700, 1);
    modules.registerSlider("TargetHud Y", "px", 220, 0, 500, 1);
    modules.registerSlider("Damage Alpha", "%", 100, 0, 150, 1);
    modules.registerSlider("Damage Effect Duration", "ticks", 37, 0, 100, 1);
    modules.registerButton("Show Avatar", true);
    modules.registerButton("Follow Player", false);
    modules.registerSlider("Background Red", "R", 30, 0, 255, 1);
    modules.registerSlider("Background Green", "G", 30, 0, 255, 1);
    modules.registerSlider("Background Blue", "B", 30, 0, 255, 1);
    modules.registerSlider("Background Alpha", "A", 180, 0, 255, 1);
    modules.registerSlider("Ghost Bar Duration", "ticks", 30, 0, 100, 1);
    modules.registerSlider("Ghost Bar Alpha", "A", 100, 0, 255, 1);
    modules.registerSlider("HealthBar Start Red", "R", 0, 0, 255, 1);
    modules.registerSlider("HealthBar Start Green", "G", 255, 0, 255, 1);
    modules.registerSlider("HealthBar Start Blue", "B", 0, 0, 255, 1);
    modules.registerSlider("HealthBar End Red", "R", 255, 0, 255, 1);
    modules.registerSlider("HealthBar End Green", "G", 0, 0, 255, 1);
    modules.registerSlider("HealthBar End Blue", "B", 0, 0, 255, 1);

}

void onPreUpdate() {
    followPlayer = modules.getButton(scriptName, "Follow Player");

    if (!followPlayer) {
        targetHudX = modules.getSlider(scriptName, "TargetHud X");
        targetHudY = modules.getSlider(scriptName, "TargetHud Y");
    }

    for (NetworkPlayer player : world.getNetworkPlayers()) {
        if (!playerHeads.containsKey(player.getUUID())) {
            playerHeads.put(player.getUUID(), new Image("https://mc-heads.net/avatar/" + player.getUUID() + ".png", true));
        }
    }
}

void onRenderTick(float partialTicks) {
 
    Entity target = modules.getKillAuraTarget();
    if (target != null) {
        if (followPlayer) {
            updateFollowPlayerPosition(target, partialTicks);
        }

        String targetDisplayName = target.getDisplayName();
        int nameWidth = render.getFontWidth(targetDisplayName);
        float hudWidth = Math.max(100, nameWidth + 30);
        float hudHeight = 32;
        float x = (float) targetHudX;
        float y = (float) targetHudY;

        boolean showAvatar = modules.getButton(scriptName, "Show Avatar");

        int bgRed = (int) modules.getSlider(scriptName, "Background Red");
        int bgGreen = (int) modules.getSlider(scriptName, "Background Green");
        int bgBlue = (int) modules.getSlider(scriptName, "Background Blue");
        int bgAlpha = (int) modules.getSlider(scriptName, "Background Alpha");
        int startRed = (int) modules.getSlider(scriptName, "HealthBar Start Red");
        int startGreen = (int) modules.getSlider(scriptName, "HealthBar Start Green");
        int startBlue = (int) modules.getSlider(scriptName, "HealthBar Start Blue");

        int endRed = (int) modules.getSlider(scriptName, "HealthBar End Red");
        int endGreen = (int) modules.getSlider(scriptName, "HealthBar End Green");
        int endBlue = (int) modules.getSlider(scriptName, "HealthBar End Blue");
        Color backgroundColor = new Color(bgRed, bgGreen, bgBlue, bgAlpha);
        float totalHudWidth = showAvatar ? hudWidth + 32 : hudWidth;

        render.rect(x, y, x + totalHudWidth - 15, y + hudHeight, backgroundColor.getRGB());

        float targetHealth = target.getHealth();
        float maxHealth = target.getMaxHealth();
        int damageEffectDuration = (int) modules.getSlider(scriptName, "Damage Effect Duration");

        float previousHealth = lastTargetHealth;

lastTargetHealth = targetHealth;

if (previousHealth > targetHealth) { 
    damageTicks = damageEffectDuration;
    ghostBarTicks = (int) modules.getSlider(scriptName, "Ghost Bar Duration");
    lastHealthBarFillWidth = currentHealthBarFillWidth;
}
if (ghostBarTicks > 0) ghostBarTicks--;
if (damageTicks > 0) damageTicks--;


        float nameXPosition = showAvatar ? x + 26 : x + 5;
        if (showAvatar) {
            Image avatar = playerHeads.get(target.getUUID());
            if (avatar != null) {
                render.image(avatar, x + 3, y + 3, 20, 20);

                if (damageTicks > 0) {
                    int damageAlpha = (int) (modules.getSlider(scriptName, "Damage Alpha") * ((float) damageTicks / damageEffectDuration));
                    render.rect(x + 3, y + 3, x + 23, y + 23, new Color(255, 0, 0, damageAlpha).getRGB());
                }
            }
        }

        render.text2d(targetDisplayName, nameXPosition, y + 3, 0.9f, new Color(255, 255, 255).getRGB(), true);

        float healthBarWidth = hudWidth + 10;
        float targetHealthBarFillWidth = (targetHealth / maxHealth) * healthBarWidth;
        currentHealthBarFillWidth = lerp(currentHealthBarFillWidth, targetHealthBarFillWidth, 0.1f);

        float healthBarY = y + hudHeight - 6;
        render.rect(x + 2, healthBarY, x + 2 + healthBarWidth, healthBarY + 3, new Color(50, 50, 50).getRGB());


       
        if (ghostBarTicks > 0) {
    int ghostAlpha = (int) modules.getSlider(scriptName, "Ghost Bar Alpha");

    Color ghostStartColor = new Color(startRed, startGreen, startBlue, ghostAlpha);
    Color ghostEndColor = new Color(endRed, endGreen, endBlue, ghostAlpha);

    render.gradientRect(
        x + 2 + currentHealthBarFillWidth, healthBarY, 
        x + 2 + lastHealthBarFillWidth, healthBarY + 3,
        ghostStartColor.getRGB(),
        ghostEndColor.getRGB()
    );
}



       render.gradientRect(x + 2, healthBarY, x + 2 + currentHealthBarFillWidth, healthBarY + 3,
    new Color(startRed, startGreen, startBlue).getRGB(),
    new Color(endRed, endGreen, endBlue).getRGB()
);

  Entity playerIg = client.getPlayer();
if (playerIg != null) {
    float playerHealth = playerIg.getHealth();
    float healthDifference = playerHealth - targetHealth;
    String healthDiffText = String.format("%+.1f", healthDifference);
    float healthDiffX = x + 2 + currentHealthBarFillWidth - render.getFontWidth(healthDiffText);

    float healthDiffY = healthBarY - 8;
    
  
    render.text2d(healthDiffText, healthDiffX, healthDiffY, 0.9f, new Color(255, 255, 255).getRGB(), true);
}



  
        float itemX = nameXPosition;
        float itemY = y + 11;
        float itemScale = 0.8f;
        float itemSpacing = 13;

        ItemStack mainHandItem = target.getHeldItem();
        if (mainHandItem != null) {
            render.item(mainHandItem, itemX, itemY, itemScale);
            itemX += itemSpacing;
        }

        for (int i = 3; i >= 0; i--) {
            ItemStack armor = target.getArmorInSlot(i);
            if (armor != null) {
                render.item(armor, itemX, itemY, itemScale);
            }
            itemX += itemSpacing;
        }
    }
}

double interpolate(double current, double old, float scale) {
    return old + (current - old) * scale;
}

float lerp(float start, float end, float speed) {
    return start + (end - start) * speed;
}

void updateFollowPlayerPosition(Entity entity, float partialTicks) {
    Vec3 position = entity.getPosition();
    Vec3 lastPosition = entity.getLastPosition();

    double interpolatedX = interpolate(position.x, lastPosition.x, partialTicks);
    double interpolatedY = interpolate(position.y, lastPosition.y, partialTicks);
    double interpolatedZ = interpolate(position.z, lastPosition.z, partialTicks);

    Vec3 screenPos = render.worldToScreen(interpolatedX, interpolatedY + entity.getHeight(), interpolatedZ, client.getDisplaySize()[2], partialTicks);

    if (screenPos != null) {
        targetHudX = screenPos.x - 50;
        targetHudY = screenPos.y - 25;
    }
}