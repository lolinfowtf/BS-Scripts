double PositionX, PositionY;
float currentHealthBarFillWidth = 1.0f;
HashMap<String, Image> playerHeads = new HashMap<>();
float lastHealthBarFillWidth = 1.0f;
int ghostBarTicks = 0;
int damageTicks = 0;
float lastTargetHealth = -1;
boolean followPlayer = false;
String[] Colors = {"Cherry", "Cotton Candy", "Flare", "Flower", "Gold", "Grayscale", "Royal", "Sky", "Vine"};

void onLoad() {
    modules.registerSlider("Position X", 250, 0, 700, 1);
    modules.registerSlider("Position Y", 220, 0, 500, 1);
    modules.registerButton("Show Avatar", true);
    modules.registerButton("Follow Player", false);
    modules.registerSlider("Background Alpha", 180, 0, 255, 1);
    modules.registerSlider("Ghost Bar Duration", " Ticks", 30, 0, 100, 1);
    modules.registerSlider("Ghost Bar Alpha", 100, 0, 255, 1);
    modules.registerSlider("Color Mode", "", 0, Colors);
}

Color[][] colorModes = {
    { new Color(255, 200, 200), new Color(243, 58, 106) }, 
    { new Color(99, 249, 255), new Color(255, 104, 204) }, 
    { new Color(231, 39, 24), new Color(245, 173, 49) }, 
    { new Color(215, 166, 231), new Color(211, 90, 232) }, 
    { new Color(255, 215, 0), new Color(240, 159, 0) }, 
    { new Color(240, 240, 240), new Color(110, 110, 110) }, 
    { new Color(125, 204, 241), new Color(30, 71, 170) }, 
    { new Color(160, 230, 225), new Color(15, 190, 220) }, 
    { new Color(17, 192, 45), new Color(201, 234, 198) }
};


void onPreUpdate() {
    followPlayer = modules.getButton(scriptName, "Follow Player");

    if (!followPlayer) {
        PositionX = modules.getSlider(scriptName, "Position X");
        PositionY = modules.getSlider(scriptName, "Position Y");
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
        float x = (float) PositionX;
        float y = (float) PositionY;

        boolean showAvatar = modules.getButton(scriptName, "Show Avatar");

        int bgAlpha = (int) modules.getSlider(scriptName, "Background Alpha");
        Color backgroundColor = new Color(30, 30, 30, bgAlpha); 
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
                    render.rect(x + 3, y + 3, x + 23, y + 23, new Color(255, 0, 0, 100).getRGB());
                }
            }
        }

        render.text2d(targetDisplayName, nameXPosition, y + 3, 0.9f, new Color(255, 255, 255).getRGB(), true);

        float healthBarWidth = hudWidth + 10;
        float targetHealthBarFillWidth = (targetHealth / maxHealth) * healthBarWidth;
        currentHealthBarFillWidth = lerp(currentHealthBarFillWidth, targetHealthBarFillWidth, 0.1f);

        float healthBarY = y + hudHeight - 6;
        render.rect(x + 2, healthBarY, x + 2 + healthBarWidth, healthBarY + 3, new Color(50, 50, 50).getRGB());
        int colorMode = (int) modules.getSlider(scriptName, "Color Mode");
        Color startColor = colorModes[colorMode][0];
        Color endColor = colorModes[colorMode][1];


        if (ghostBarTicks > 0) {
            int ghostAlpha = (int) modules.getSlider(scriptName, "Ghost Bar Alpha");

            Color ghostStartColor = new Color(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), ghostAlpha);
            Color ghostEndColor = new Color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), ghostAlpha);

            render.gradientRect(x + 2 + currentHealthBarFillWidth, healthBarY, 
                x + 2 + lastHealthBarFillWidth, healthBarY + 3,
                ghostStartColor.getRGB(), ghostEndColor.getRGB());
        }

   
        render.gradientRect(x + 2, healthBarY, x + 2 + currentHealthBarFillWidth, healthBarY + 3,
            startColor.getRGB(), endColor.getRGB());

 
        Entity playerIg = client.getPlayer();
        if (playerIg != null) {
            float playerHealth = playerIg.getHealth();
            float healthDifference = playerHealth - targetHealth;
            String healthDiffText = String.format("%+.1f", healthDifference);

            float healthDiffX = x + 5 + healthBarWidth - render.getFontWidth(healthDiffText) - 2;
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
        PositionX = screenPos.x - 50;
        PositionY = screenPos.y - 25;
    }
}