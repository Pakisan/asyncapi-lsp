import * as vscode from "vscode";
import { workspace, ExtensionContext } from 'vscode';
import * as net from 'net';
import {
    LanguageClient,
    LanguageClientOptions,
    StreamInfo
} from 'vscode-languageclient/node';

let client: LanguageClient;

export function activate(context: ExtensionContext) {
    const config = vscode.workspace.getConfiguration();
    const serverPort: string = config.get("serverPort"); // Получаем порт из настроек окружения vscode
    vscode.window.showInformationMessage(`Starting LSP client on port: ` + serverPort);  // Отправим пользователю информацию о запуске расширения

    const connectionInfo = {
        port: Number(serverPort),
        host: "localhost"
    };
    const serverOptions = () => {
        // Подключение по сокету
        const socket = net.connect(connectionInfo);
        const result: StreamInfo = {
            writer: socket,
            reader: socket
        };
        return Promise.resolve(result);
    };

    const clientOptions: LanguageClientOptions = {
        documentSelector: [
            { scheme: 'file', language: 'yml' },
            { scheme: 'file', language: 'yaml' },
            { scheme: 'file', language: 'json' },
            { scheme: 'file', language: 'plaintext' },
        ], // Указываем расширение файлов, с которыми поддерживаем работу
        synchronize: {
            fileEvents: workspace.createFileSystemWatcher('**/.clientrc')
        }
    };

    client = new LanguageClient(
        'languageServerExample',
        'Language Server Example',
        serverOptions,
        clientOptions
    );

    client.start();
}

export function deactivate(): Thenable<void> | undefined {
    if (!client) {
        return undefined;
    }
    return client.stop();
}