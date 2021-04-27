"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.start = void 0;
const vscode = require("vscode");
const net = require("net");
const vscode_languageclient_1 = require("vscode-languageclient");
const cp = require("child_process");
const path = require("path");
const fs = require("fs");
const property_error_1 = require("./property-error");
const LANGUAGE_ID = 'kukumo-gherkin';
const PROPERTIES_SECTION = 'kukumo.languageServer';
const PROPERTY_CONNECTION_MODE = 'kukumo.languageServer.connectionMode';
const PROPERTY_TCP_CONNECTION = 'kukumo.languageServer.TCPConnection';
const PROPERTY_JAVA_PLUGIN_PATH = 'kukumo.languageServer.javaProcessPluginPath';
const CONNECTION_MODE = {
    tcp: "TCP Connection",
    java: "Java Process"
};
const LANGUAGE_CLIENT_OPTIONS = {
    documentSelector: [
        {
            scheme: 'file',
            language: LANGUAGE_ID
        },
        {
            scheme: 'file',
            language: 'yaml'
        }
    ],
    initializationFailedHandler: onLanguageClientInitializacionFailed,
    errorHandler: {
        error: onLanguageClientError,
        closed: onLanguageClientClosed
    },
    synchronize: {
        fileEvents: vscode.workspace.createFileSystemWatcher('*')
    },
    progressOnInitialization: true
};
const STATUS_BAR_TEXT_OFFLINE = 'KLS 🔌';
const STATUS_BAR_TEXT_ONLINE = 'KLS 🗸';
var client;
var statusBar;
function start(context) {
    statusBar = vscode.window.createStatusBarItem(vscode.StatusBarAlignment.Right);
    statusBar.text = STATUS_BAR_TEXT_OFFLINE;
    statusBar.command = 'kukumo.reconnectLanguageServer';
    statusBar.tooltip = 'Connection with Kukumo Language Server';
    statusBar.show();
    listenPropertiesChanges(context);
    registerCommands(context);
    startLanguageClient(context);
}
exports.start = start;
function registerCommands(context) {
    context.subscriptions.push(vscode.commands.registerCommand('kukumo.reconnectLanguageServer', () => startLanguageClient(context)));
}
function startLanguageClient(context) {
    if (client) {
        client.stop();
    }
    try {
        const connectionMode = vscode.workspace.getConfiguration().get(PROPERTY_CONNECTION_MODE, '');
        if (connectionMode === '') {
            throw new property_error_1.PropertyError(PROPERTY_CONNECTION_MODE, 'not configured');
        }
        else if (connectionMode === CONNECTION_MODE.tcp) {
            startTcpLanguageClient(context);
        }
        else if (connectionMode === CONNECTION_MODE.java) {
            startJavaProcessLanguageCLiente(context);
        }
    }
    catch (error) {
        if (error instanceof property_error_1.PropertyError) {
            error.showError();
        }
        else {
            vscode.window.showErrorMessage(`${error}`);
        }
    }
}
function startTcpLanguageClient(context) {
    const tcpServer = vscode.workspace.getConfiguration().get(PROPERTY_TCP_CONNECTION, '');
    if (tcpServer === '') {
        throw new property_error_1.PropertyError(PROPERTY_TCP_CONNECTION, 'not configured');
    }
    const [host, port] = tcpServer.split(':', 2);
    const languageClient = new vscode_languageclient_1.LanguageClient('kukumo-languange-client', 'Kukumo Language Server Client', () => tcpServerProvider(context, host, parseInt(port)), LANGUAGE_CLIENT_OPTIONS);
    launchLanguageClient(languageClient, context);
}
function startJavaProcessLanguageCLiente(context) {
    const pluginsPath = vscode.workspace.getConfiguration().get(PROPERTY_JAVA_PLUGIN_PATH, '');
    if (pluginsPath === '') {
        throw new property_error_1.PropertyError(PROPERTY_JAVA_PLUGIN_PATH, 'not configured');
    }
    if (!fs.existsSync(pluginsPath)) {
        throw new property_error_1.PropertyError(PROPERTY_JAVA_PLUGIN_PATH, 'path does not exist');
    }
    const languageClient = new vscode_languageclient_1.LanguageClient('kukumo-languange-client', 'Kukumo Language Server Client', () => runLanguageServerAsJavaProcess(pluginsPath), LANGUAGE_CLIENT_OPTIONS);
    // languageClient.handleFailedRequest = ( (type, error, defaultValue)=> {
    //     console.log('Failed request',type,error,defaultValue);
    //     return defaultValue;
    // });
    launchLanguageClient(languageClient, context);
}
function launchLanguageClient(languageCliente, context) {
    console.log("Starting Kukumo language client...");
    languageCliente.onReady().then(() => {
        console.log('Kukumo language client ready');
        statusBar.text = STATUS_BAR_TEXT_ONLINE;
    });
    context.subscriptions.push(languageCliente.start());
    client = languageCliente;
}
function listenPropertiesChanges(context) {
    context.subscriptions.push(vscode.workspace.onDidChangeConfiguration(event => {
        if (!event.affectsConfiguration(PROPERTIES_SECTION)) {
            return;
        }
        console.log(`Property changed!`, event);
        if (event.affectsConfiguration(PROPERTY_CONNECTION_MODE)) {
            console.log('Kukumo language server connection mode has change, restarting language client...');
            if (client) {
                client.stop().then(() => startLanguageClient(context));
            }
        }
    }));
}
function tcpServerProvider(context, host, port) {
    return new Promise((resolve, reject) => {
        try {
            console.log(`Connecting to Kukumo TCP language server ${host}:${port} ...`);
            var socketClient = new net.Socket();
            socketClient.addListener('error', error => notifyConnectionLost(context, error));
            socketClient.addListener('timeout', () => notifyConnectionLost(context));
            socketClient.addListener('close', () => notifyConnectionLost(context));
            socketClient.connect(port, host, () => console.info('Connected to Kukumo language server.'));
            const streamInfo = {
                writer: socketClient,
                reader: socketClient,
                detached: false
            };
            resolve(streamInfo);
        }
        catch (error) {
            console.error('Error connecting to Kukumo TCP language server', error);
            reject(error);
        }
    });
}
function notifyConnectionLost(context, error) {
    console.log('Connection closed', error);
    statusBar.text = STATUS_BAR_TEXT_OFFLINE;
    client.stop();
    vscode.window.showWarningMessage('Connection with language server closed', 'Reconnect').then(action => {
        if (action === 'Reconnect') {
            startLanguageClient(context);
        }
    });
}
function onLanguageClientInitializacionFailed(error) {
    console.error('Error initializing Kukumo language client', error);
    return false;
}
function onLanguageClientError(error, message, count) {
    vscode.window.showErrorMessage(`Cannot activate Kukumo language client: ${error.message}`);
    console.log(error, count);
    return vscode_languageclient_1.ErrorAction.Shutdown;
}
function onLanguageClientClosed() {
    console.log('Kukumo language client closed.');
    return vscode_languageclient_1.CloseAction.DoNotRestart;
}
function runLanguageServerAsJavaProcess(pluginPath) {
    return new Promise((resolve, reject) => {
        try {
            const libPath = path.join(__dirname, '..', 'lib');
            const command = `java -p ${libPath}:${pluginPath} -m kukumo.lsp/iti.kukumo.lsp.Launcher -d`;
            console.log(command);
            const process = cp.exec(command, (error, stdout, stderr) => console.log(stderr));
            process.addListener('close', (code) => console.log(`Java process close with code ${code}`));
            process.addListener('disconnect', () => console.log(`Java process disconnected`));
            process.addListener('error', (error) => console.log(`Java process error ${error}`));
            process.addListener('exit', () => console.log(`Java process exited`));
            process.addListener('message', (message) => console.log(`Java process message ${message}`));
            resolve(process);
        }
        catch (error) {
            reject(error);
        }
    });
}
//# sourceMappingURL=language-client.js.map