
# Plugins
Place Python files in the `plugins/` folder. Each plugin should provide a register(app) function
that returns metadata and a dict of commands. The main app will load plugins dynamically.
