use serde_json::Value;
// use std::{env, fs};
// use zed::settings::LspSettings;
use zed_extension_api::{self as zed, LanguageServerId, Result, Worktree, serde_json::json};

struct AsyncAPIExtension {}

impl AsyncAPIExtension {

}

impl zed::Extension for AsyncAPIExtension {
    fn new() -> Self {
        Self {}
    }

    fn language_server_initialization_options(
        &mut self,
        _language_server_id: &LanguageServerId,
        _worktree: &Worktree
    ) -> Result<Option<Value>> {
        let initialization_options = json!({
            "diagnostics": true
        });
        Ok(Some(initialization_options))
    }

    fn language_server_command(
        &mut self,
        language_server_id: &LanguageServerId,
        worktree: &zed::Worktree,
    ) -> Result<zed::Command> {
        let mut args = Vec::new();
        let mut env = Vec::new();
        Ok(zed::Command {
            command: "asyncapi-lsp-server-1.0.0-SNAPSHOT/bin/asyncapi-lsp-server".to_string(),
            args,
            env
        })
    }
}

zed::register_extension!(AsyncAPIExtension);
