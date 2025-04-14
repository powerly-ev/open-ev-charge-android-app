
# 🈯 Android Strings Translator using Gemini

This Python script automates the translation of Android `strings.xml` files into multiple languages using Gemini API.

---

## 📦 Features

- Parses English string resources from `../common/resources/src/main/res/values/strings.xml`.
- Translates them into any number of locales using gemini.
- Saves translated files in proper Android folder structure at strings-translator/res (`values-tr/`, `values-zh/`, etc.).

---

## 🧰 Requirements

- Python 3.7+
- OpenAI API key

Install required package:

```bash
pip install google-genai
```

Set API key in Project_Root/local.properties:

```bash
GEIMIN_API_KEY=your-api-key-here
```

---

## 🚀 Usage

Run the script:

```bash
python translate_android_strings.py tr
```

## 📁 Output Folder Structure

```
res/values-tr/
 └── strings.xml                 # Trurkish translation
```

---

## 📌 License

This project is free to use and modify under the MIT License.
