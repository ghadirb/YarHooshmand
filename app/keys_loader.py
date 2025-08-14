import json
import os
import time
import requests

APP_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CACHE_DIR = os.path.join(APP_DIR, ".cache")
os.makedirs(CACHE_DIR, exist_ok=True)
CACHE_FILE = os.path.join(CACHE_DIR, "api_keys.json")

DEFAULT_KEYS = {
    "provider": "openrouter",  # "openrouter" | "openai"
    "openrouter_api_key": "",
    "openai_api_key": "",
    "openrouter_base_url": "https://openrouter.ai/api/v1",
    "openai_base_url": "https://api.openai.com/v1",
    # فقط مدل‌هایی که فارسی خوبی دارند
    "allowed_models": [
        "openrouter/auto",
        "deepseek/deepseek-chat",
        "qwen/qwen2.5-7b-instruct",
        "google/gemini-1.5-flash",
        "meta-llama/llama-3.1-8b-instruct"
    ],
    "preferred_model": "openrouter/auto",
    "updated_at": 0
}

class KeysManager:
    def __init__(self, remote_url: str):
        self.remote_url = remote_url

    def _read_cache(self):
        if os.path.exists(CACHE_FILE):
            try:
                with open(CACHE_FILE, "r", encoding="utf-8") as f:
                    return json.load(f)
            except Exception:
                return DEFAULT_KEYS.copy()
        return DEFAULT_KEYS.copy()

    def _write_cache(self, data: dict):
        data["updated_at"] = int(time.time())
        with open(CACHE_FILE, "w", encoding="utf-8") as f:
            json.dump(data, f, ensure_ascii=False, indent=2)

    def refresh_from_remote(self) -> bool:
        """
        فایل متنی/JSON توی گوگل‌درایو را دانلود می‌کند.
        فرمت پیشنهادی فایل گوگل‌درایو:
        {
          "provider": "openrouter",
          "openrouter_api_key": "XXXX",
          "allowed_models": ["openrouter/auto", "deepseek/deepseek-chat"],
          "preferred_model": "openrouter/auto"
        }
        اگر فایل ساده متنی با خطوط key=value هم باشد، خوانده می‌شود.
        """
        try:
            r = requests.get(self.remote_url, timeout=20)
            r.raise_for_status()
            text = r.text.strip()
            data = None
            # اول تلاش JSON
            try:
                data = json.loads(text)
            except Exception:
                # fallback: خطوط key=value
                obj = {}
                for line in text.splitlines():
                    if "=" in line:
                        k, v = line.split("=", 1)
                        obj[k.strip()] = v.strip()
                if obj:
                    data = {**DEFAULT_KEYS, **obj}

            if not data:
                # چیزی ننشست، کش قبلی را نگه می‌داریم
                return False

            merged = {**DEFAULT_KEYS, **data}
            self._write_cache(merged)
            return True
        except Exception:
            return False

    def get(self) -> dict:
        return self._read_cache()
