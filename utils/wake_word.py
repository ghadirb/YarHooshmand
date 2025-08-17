
"""
Wake-word (Porcupine) placeholder.

To enable Porcupine:
- Obtain Porcupine SDK and keyword files for your wake word from Picovoice (Porcupine).
- On Android you may include native libraries and call them via JNI or run a small native service.
- Alternatively consider using a lightweight on-device keyword detector that provides Python bindings.

This module provides a placeholder API:
- start_listener(callback) : starts wake-word listener and calls callback() when detected.
- stop_listener() : stops listener.

Real implementation is left to the integrator due to licensing and native dependencies.
"""
import threading

_listener_thread = None
_running = False

def start_listener(callback):
    global _listener_thread, _running
    if _running:
        return False
    _running = True
    def _fake_loop():
        # placeholder loop: does nothing, no wake-word detection
        while _running:
            import time; time.sleep(1)
    _listener_thread = threading.Thread(target=_fake_loop, daemon=True)
    _listener_thread.start()
    return True

def stop_listener():
    global _running
    _running = False
    return True
