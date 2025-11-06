# Java VS Code Demo-Projekt mit Sockets

Struktur mit drei Packages:

- `de.hsrm.demo.client`  → `ClientMain` (TCP-Client)
- `de.hsrm.demo.server`  → `ServerMain` (TCP-Server)
- `de.hsrm.demo.coded`   → `MessageCodec` (von Client und Server importiert)

Die Kommunikation läuft über TCP (localhost:5000), Nachrichten werden mit `MessageCodec`
kodiert (`[ENC]...[/ENC]`) und als eine Zeile (mit `println`) übertragen.

## Öffnen in VS Code

1. ZIP entpacken.
2. In VS Code: **File → Open Folder...** und den entpackten Ordner `java-vscode-sockets-demo` wählen.
3. Falls noch nicht vorhanden: Java Extension Pack installieren.
4. Im Explorer zuerst `ServerMain.java` öffnen und oben auf **Run** / ▶️ klicken.
5. Danach `ClientMain.java` öffnen und ebenfalls **Run** / ▶️ klicken.

Konsolenausgabe:

- Server: zeigt roh empfangene und dekodierte Nachricht des Clients.
- Client: zeigt roh empfangene und dekodierte Antwort des Servers.

## Kompilieren und Ausführen per Terminal

Im Projekt-Root:

```bash
javac -d out $(find src -name "*.java")
java -cp out de.hsrm.demo.server.ServerMain
# in einem zweiten Terminal:
java -cp out de.hsrm.demo.client.ClientMain
```
