import os
import sys
import xml.etree.ElementTree as ET
import time
import argparse
from google import genai

def get_api_key_from_local_properties():
    with open('../local.properties', 'r') as file:
        lines = file.readlines()
        for line in lines:
            if line.startswith('GEIMIN_API_KEY'):  # Assuming geimin uses GEIMIN_API_KEY
                return line.strip().split('=')[1]
        return None

# Load your Geimin API key
api_key = get_api_key_from_local_properties()
print(api_key)
client = genai.Client(api_key=api_key)

def translate_text(text, target_lang):
    """
    Translates given text into a target language using the geimin library.

    Args:
        text (str): The full string of key-value pairs to translate.
        target_lang (str): The target language code (e.g., 'fr', 'es').

    Returns:
        str: The translated text as a string with key-value format.
    """
    # Construct the prompt or input format expected by geimin
    prompt = f"Translate the following English Android app strings to {target_lang}.\n\n{text}\n\nReturn full translated xml content and make sure to escape all sigle qoutes '."

    try:
        response = client.models.generate_content(
                model="gemini-2.0-flash",
                contents=[prompt] 
        )
        translation_result = response.text.replace('```xml','').replace('```','').strip()

        if translation_result:
            return translation_result  # Adjust based on geimin's output format
        else:
            print("Translation failed.")
            return None

    except Exception as e:
        print(f"Error translating with geimin: {e}")
        return None

# python strings_translator.py en ar es fr zh-CN hi de ja ko pt th vi id tr
def main(en_file_path, locales):
    print("Reading English strings...")
    en_strings =open(en_file_path,'r',encoding='utf-8').read()
    allowed_locales = ['zh-CN', 'hi', 'de', 'ja', 'ko', 'pt', 'th', 'vi', 'id', 'tr']
    for locale in locales:
        if locale not in allowed_locales:continue
        print(f"Translating to {locale}...")
        locale_dir = "res/values-%s"%locale
        if not os.path.exists(locale_dir):os.makedirs(locale_dir)
        translated = translate_text(en_strings, locale)
        output_file = locale_dir+"/strings.xml"
        open(output_file,'w',encoding='utf-8').write(translated)
        print(f"Saved: {output_file}")
        time.sleep(1)  # Avoid API rate limit

if __name__ == "__main__":
    # Set up CLI argument parsing
    parser = argparse.ArgumentParser(description="Translate Android strings.xml to multiple languages using geimin.")
    parser.add_argument(
        "locales",
        metavar="locale",
        type=str,
        nargs="+",
        help="List of target locales (e.g., ar en tr)"
    )
    args = parser.parse_args()

    # Define the path to the English strings.xml
    en_file = "../common/resources/src/main/res/values/strings.xml"

    # Call the main function with the provided locales
    main(en_file, args.locales)