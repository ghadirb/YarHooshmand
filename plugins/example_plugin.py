
"""
Example plugin: provides a simple command that the app can call.
Plugins must expose a `register(app)` function.
"""
def register(app):
    return {
        "name": "Example Plugin",
        "version": "0.1",
        "commands": {
            "hello": lambda args: f"Hello from plugin! args={args}"
        }
    }
