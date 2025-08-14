import requests
import os

class SmartClient:
    def __init__(self, keys_manager):
        self.keys_manager = keys_manager

    def _headers_and_url(self):
        keys = self.keys_manager.get()
        provider = (keys.get("provider") or "openrouter").lower()
        model = keys.get("preferred_model") or (keys.get("allowed_models") or ["openrouter/auto"])[0]

        if provider == "openai":
            base = keys.get("openai_base_url") or "https://api.openai.com/v1"
            api_key = keys.get("openai_api_key") or os.getenv("OPENAI_API_KEY", "")
            headers = {"Authorization": f"Bearer {api_key}"}
            url = f"{base}/chat/completions"
        else:
            # پیش‌فرض openrouter
            base = keys.get("openrouter_base_url") or "https://openrouter.ai/api/v1"
            api_key = keys.get("openrouter_api_key") or os.getenv("OPENROUTER_API_KEY", "")
            headers = {"Authorization": f"Bearer {api_key}"}
            url = f"{base}/chat/completions"

        return headers, url, model

    def chat(self, user_text: str) -> str:
        headers, url, model = self._headers_and_url()
        if not headers.get("Authorization"):
            return "کلید API تنظیم نشده است. لطفاً فایل گوگل‌درایو را اصلاح کنید."

        payload = {
            "model": model,
            "messages": [
                {"role": "system", "content": "تو یک دستیار فارسی‌زبان هستی. پاسخ‌ها کوتاه، دقیق و مودبانه باشند."},
                {"role": "user", "content": user_text}
            ],
            "temperature": 0.4
        }

        try:
            r = requests.post(url, json=payload, headers=headers, timeout=60)
            r.raise_for_status()
            data = r.json()
            # OpenAI / OpenRouter هر دو فیلد choices دارند
            return data["choices"][0]["message"]["content"].strip()
        except Exception as e:
            return f"خطای شبکه/مدل: {e}"
