float chatBoxX = 1, chatBoxY = 100;
float chatBoxWidth = 250, chatBoxHeight = 200;
ArrayList<String> chatLog = new ArrayList<>();
String currentInput = "";
boolean typing = false;

HashMap<Integer, Integer> keyTimers = new HashMap<>();
int initialDelay = 12;
int repeatRate = 2;
int enterDelay = 10;
int enterTimer = 0;


WebSocket w;
List<String> q = new ArrayList<>();
long l = 0, lr = 0;
boolean rc = false;
String P = "&6&lIRC&r", k, prefix;

void onLoad() {

    modules.registerDescription("https://discord.gg/Y2BRdNcns2");
    modules.registerDescription("Use /api-key with Dave H bot");
    modules.registerDescription("Try /irc");

    prefix = config.get("irc_prefix");
    if (prefix == null || prefix.isEmpty() || prefix.length() > 1) {
        prefix = "-";
        config.set("irc_prefix", prefix);
    }
}



void onEnable() {
    addChatMessage("How to use");
     addChatMessage("Join https://discord.gg/Y2BRdNcns2, and use ");
     addChatMessage("/api-key with Dave H bot, and then ");
     addChatMessage("type here, api-key (your api key)");
     addChatMessage("--------------Credits--------------");
    addChatMessage("Chatbox made by lolinfo, IRC by pug");
    k = config.get("irc_key");
    if (k == null || k.isEmpty()) {
        client.print(P + " &7No API key found. Use &6/irc key <key> &7to set it.");
        return;
    }
    if (w != null && w.isOpen()) return;
    connectIRC();
}

void connectIRC() {
    if (w != null) {
        w.close(false);
        w = null;
    }
    w = new WebSocket("wss://privatemethod.xyz/api/irc?key=" + k) {
        public void onOpen(short s, String m) {
            rc = false;
            q.clear();
        }

        public void onMessage(String m) {
            if (modules.isEnabled(scriptName)) processIRCMessage(m);
        }

        public void onClose(int c, String rs, boolean rem) {
            client.print(P + " &7Disconnected.");
            rc = true;
            lr = client.time();
        }
    };
    w.connect(false);
}

void onRenderTick(float partialTicks) {
    int bgAlpha = 120;
    int backgroundColor = (bgAlpha << 24);

    render.rect(chatBoxX, chatBoxY, chatBoxWidth, chatBoxHeight, backgroundColor);

    int topRectangleColor = (255 << 24) | (173 << 16) | (216 << 8) | 230;
    int topRectangleX = (int) (chatBoxX + 3);
    int topRectangleY = (int) (chatBoxY + 2);
    render.rect(topRectangleX, topRectangleY, chatBoxWidth - 3, 100, topRectangleColor);

    float textY = chatBoxY + 5;
    int maxLines = (int) (chatBoxHeight / 23);
    
    while (chatLog.size() > maxLines) {
        chatLog.remove(0);
    }

    int start = Math.max(0, chatLog.size() - maxLines);
    for (int i = start; i < chatLog.size(); i++) {
        render.text2d(chatLog.get(i), chatBoxX + 5, textY, 1.0f, 0xFFFFFFFF, true);
        textY += 10;
    }

    if (typing) {
        render.text2d(currentInput + (System.currentTimeMillis() % 1000 < 500 ? "_" : ""), 
            chatBoxX + 5, textY, 1.0f, 0xFFFFFFFF, true);
    } else {
        render.text2d("Press ENTER to send a message", chatBoxX + 5, chatBoxY + chatBoxHeight - 112, 1.0f, 0xAAAAAA, true);
    }
}

void c(){
    if(w != null){ w.close(false); w = null; }
    w = new WebSocket("wss://privatemethod.xyz/api/irc?key="+k){
        public void onOpen(short s, String m){ rc = false; q.clear(); }
        public void onMessage(String m){ if(modules.isEnabled(scriptName)) mP(m); }
        public void onClose(int c, String rs, boolean rem){
            client.print(P + " &7Disconnected.");
            rc = true; lr = client.time();
        }
    };
    w.connect(false);
}

void onPostPlayerInput() {
    boolean shift = keybinds.isKeyDown(42) || keybinds.isKeyDown(54);

    if (enterTimer > 0) enterTimer--;

    if (keybinds.isKeyDown(28) && !typing && enterTimer == 0) {
        typing = true;
        currentInput = "";
        enterTimer = enterDelay;
        return;
    }

    if (typing) {
        keybinds.setPressed("key.forward", false);
        keybinds.setPressed("key.back", false);
        keybinds.setPressed("key.left", false);
        keybinds.setPressed("key.right", false);
        keybinds.setPressed("key.jump", false);
        keybinds.setPressed("key.sneak", false);
        keybinds.setPressed("key.inventory", false);
        for (int i = 0; i < 9; i++) {
            keybinds.setPressed("key.hotbar." + i, false);
        }

        for (int key = 1; key < 256; key++) {
            try {
                if (keybinds.isKeyDown(key)) {
                    if (key == 28) {
                        if (enterTimer == 0) {
                            handleEnterKey();
                            typing = false;
                            enterTimer = enterDelay;
                        }
                        return;
                    }

                    if (!keyTimers.containsKey(key)) {
                        keyTimers.put(key, 0);
                        processKeyInput(key, shift);
                    } else {
                        int timer = keyTimers.get(key);
                        if (timer >= initialDelay && (timer - initialDelay) % repeatRate == 0) {
                            processKeyInput(key, shift);
                        }
                        keyTimers.put(key, timer + 1);
                    }
                } else {
                    keyTimers.remove(key);
                }
            } catch (Exception e) {
                client.print("Invalid key index: " + key);
            }
        }
        return;
    }
}

void handleEnterKey() {
    if (!typing) {
        typing = true;
    } else {
        if (!currentInput.isEmpty()) {
            if (currentInput.length() > 256) {
                addChatMessage("§cError sending message: too long");
            } else {
                sendIRCMessage(currentInput);
            }
            currentInput = "";
        }
        typing = false;
    }
}

void processKeyInput(int key, boolean shift) {
    if (key == 14) {
        if (!currentInput.isEmpty()) {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
        }
    } else {
        String character = convertKeyToChar(key, shift);
        if (!character.isEmpty()) {
            currentInput += character;
        }
    }
}

void processIRCMessage(String m) {
    Json j = new Json(m);
    if (!j.exists()) {
        client.print(P + " &cInvalid JSON: " + m);
        return;
    }
    String d = j.get("data", "");
    addChatMessage("[IRC] " + d);
}

void sendIRCMessage(String msg) {
    if (w != null && w.isOpen()) {
        w.send("{\"id\":0,\"data\":\"" + escape(msg) + "\"}");
    }
}

void addChatMessage(String message) {
    chatLog.add(message);
}

void onPreUpdate() {
    long t = client.time();
    if (w != null && w.isOpen() && t - l > 1000 && !q.isEmpty()) {
        sendIRCMessage(q.remove(0));
        l = t;
    }
    if (rc && t - lr > 5000) {
        client.print(P + " &7Attempting to reconnect...");
        lr = t;
        connectIRC();
    }
}

String escape(String s) {
    return s.replace("\\", "\\\\").replace("\"", "\\\"");
}

String convertKeyToChar(int key, boolean shift) {
    String normal = "1234567890-=[]\\;',./";
    String shifted = "!@#$%^&*()_+{}|:\"<>?";

    if (key >= 2 && key <= 11) return shift ? "" + shifted.charAt(key - 2) : "" + normal.charAt(key - 2);
    if (key >= 16 && key <= 25) return shift ? ("" + "QWERTYUIOP".charAt(key - 16)) : ("" + "qwertyuiop".charAt(key - 16));
    if (key >= 30 && key <= 38) return shift ? ("" + "ASDFGHJKL".charAt(key - 30)) : ("" + "asdfghjkl".charAt(key - 30));
    if (key >= 44 && key <= 50) return shift ? ("" + "ZXCVBNM".charAt(key - 44)) : ("" + "zxcvbnm".charAt(key - 44));
    if (key == 57) return " ";
    if (key == 41) return shift ? "~" : "`";
    if (key == 39) return shift ? ":" : ";";
    if (key == 51) return shift ? "<" : ",";
    if (key == 52) return shift ? ">" : ".";
    if (key == 53) return shift ? "?" : "/";
    return "";
}
boolean onChat(String msg) {

    Entity player = client.getPlayer();
    String name = player.getName(); 


    String cleanedMsg = msg.replaceAll("&[0-9a-fklmnor]", ""); 

    if (cleanedMsg.startsWith("<" + name + "> ")) {
        cleanedMsg = cleanedMsg.substring(name.length() + 3);
    }

    if (cleanedMsg.matches("(?i)^(&6&lIRC|IRC|&6&lIRC&r)\\s.*")) {
        String messageContent = cleanedMsg.replaceFirst("(?i)^(&6&lIRC|IRC|&6&lIRC&r)\\s*", "");
        client.print("Filtered message: " + messageContent);
        addChatMessage(messageContent);
    }

    return true; 
}
void mP(String m){
    Json j = new Json(m);
    if(!j.exists()){
        client.print(P + " &cInvalid JSON: " + m);
        return;
    }
    int id = Integer.parseInt(j.get("id", "-1"));
    String d = j.get("data", "");
    if(id == 0) {
        Message ms = new Message(d);
        client.print(ms);
    }
}

String genPad(char ch, int px){
    StringBuilder sb = new StringBuilder();
    int cw = render.getFontWidth(String.valueOf(ch));
    int n = px / cw;
    for(int i = 0; i < n; i++) sb.append(ch);
    return sb.toString();
}

boolean onPacketSent(CPacket p){
    if(p instanceof C01){
        C01 c = (C01)p;
        
        if(c.message.startsWith(prefix)){
            q.add(c.message.substring(prefix.length()));
            return false;
        }
        
        if(!c.message.startsWith("/irc")) return true;
        String[] a = c.message.split(" ", 3);
        
        if(a.length <= 1){
            String t = " &6IRC Commands &7", f = " &6Made by Pug &7";
            String[] m = {
                "&6/irc key <key>&7: Sets your IRC API key.",
                "&6/irc name <name>&7: Sets your IRC username.",
                "&6/irc prefix <symbol>&7: Sets the IRC message prefix."
            };
            int mx = 0;
            String st = t.replaceAll("&[0-9a-fk-or]", ""), sf = f.replaceAll("&[0-9a-fk-or]", "");
            for(String s : m) mx = Math.max(mx, render.getFontWidth(s.replaceAll("&[0-9a-fk-or]", "")));
            mx = Math.max(mx, render.getFontWidth(st));
            mx = Math.max(mx, render.getFontWidth(sf));
            int ex = render.getFontWidth("  "), hw = mx + ex;
            int tp = hw - render.getFontWidth(st), fp = hw - render.getFontWidth(sf);
            int tsp = tp / 2, fsp = fp / 2;
            String hdr = "&7" + genPad('-', tsp) + t + genPad('-', tsp);
            if(tp % 2 != 0) hdr += "-";
            client.print(P + " " + hdr);
            for(String s : m){
                int lp = (mx - render.getFontWidth(s.replaceAll("&[0-9a-fk-or]", ""))) / 2;
                client.print(P + " " + genPad(' ', lp) + s);
            }
            String ftr = "&7" + genPad('-', fsp) + f + genPad('-', fsp);
            if(fp % 2 != 0) ftr += "-";
            client.print(P + " " + ftr);
            return false;
        }

        String sub = a[1].trim().toLowerCase();
        if(sub.equals("key")){
            if(a.length < 3 || a[2].trim().isEmpty()){
                client.print(P + " &7 Usage: &6/irc key <key>");
                return false;
            }
            k = a[2].trim();
            if(config.set("irc_key", k)){
                client.print(P + " &7API key updated. Connecting...");
                c();
            } else client.print(P + " &7Failed to save API key.");
            return false;
        } else if(sub.equals("name")){
            if(a.length < 3 || a[2].trim().isEmpty()){
                client.print(P + " &7Usage: &6/irc name <name>");
                return false;
            }
            sendIRC(1, a[2].trim());
            return false;
        } else if(sub.equals("prefix")){
            if(a.length < 3 || a[2].trim().isEmpty()){
                client.print(P + " &7Usage: &6/irc prefix <symbol>");
                return false;
            }
            String newPrefix = a[2].trim();
            
            if(newPrefix.length() > 1){
                client.print(P + " &7Invalid prefix.");
                return false;
            }

            prefix = newPrefix;
            config.set("irc_prefix", prefix);
            client.print(P + " &7Prefix updated to: &6" + prefix);
            return false;
        } else {
            client.print(P + " &7Unknown subcommand: " + sub);
            return false;
        }
    }
    return true;
}


void sendIRC(int id, String d){
    if(w != null && w.isOpen()){
        w.send("{\"id\":"+id+",\"data\":\""+e(d)+"\"}");
    }
}

String e(String s){ return s.replace("\\", "\\\\").replace("\"", "\\\""); }
