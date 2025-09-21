#!/usr/bin/env python3
import csv
import random
import uuid


LANGUAGE_CODES_FOR_TRANSLATIONS = ["es", "en", "ru"]
LANGUAGE_CODES_FOR_TABLE = ["ru", "en", "es"]

BASE_TRANSLATIONS: dict[str, dict[str, str]] = {
    "Autonomía": {"en": "Autonomy", "ru": "Автономия"},
    "Provincia": {"en": "Province", "ru": "Провинция"},
    "Municipio": {"en": "Municipality", "ru": "Муниципалитет"},
    "Sector": {"en": "Sector", "ru": "Сектор"},
}

AUTONOMY_TRANSLATIONS: dict[str, dict[str, str]] = {
    "Andalucía": {"en": "Andalusia", "ru": "Андалусия"},
    "Aragón": {"en": "Aragon", "ru": "Арагон"},
    "Principado de Asturias": {"en": "Principality of Asturias", "ru": "Княжество Астурия"},
    "Illes Balears": {"en": "Balearic Islands", "ru": "Балеарские острова"},
    "País Vasco": {"en": "Basque Country", "ru": "Страна Басков"},
    "Canarias": {"en": "Canary Islands", "ru": "Канарские острова"},
    "Cantabria": {"en": "Cantabria", "ru": "Кантабрия"},
    "Castilla y León": {"en": "Castile and León", "ru": "Кастилия и Леон"},
    "Castilla-La Mancha": {"en": "Castile-La Mancha", "ru": "Кастилия-Ла-Манча"},
    "Cataluña": {"en": "Catalonia", "ru": "Каталония"},
    "Comunitat Valenciana": {"en": "Valencian Community", "ru": "Валенсийское сообщество"},
    "Extremadura": {"en": "Extremadura", "ru": "Эстремадура"},
    "Galicia": {"en": "Galicia", "ru": "Галисия"},
    "Comunidad de Madrid": {"en": "Community of Madrid", "ru": "Мадридское сообщество"},
    "Región de Murcia": {"en": "Region of Murcia", "ru": "Регион Мурсия"},
    "Comunidad Foral de Navarra": {"en": "Chartered Community of Navarre", "ru": "Форалльное сообщество Наварры"},
    "La Rioja": {"en": "La Rioja", "ru": "Ла-Риоха"},
    "Ceuta": {"en": "Ceuta", "ru": "Сеута"},
    "Melilla": {"en": "Melilla", "ru": "Мелилья"},
}

PROVINCE_TRANSLATIONS: dict[str, dict[str, str]] = {
    "Almería": {"en": "Almeria", "ru": "Альмерия"},
    "Cádiz": {"en": "Cadiz", "ru": "Кадис"},
    "Córdoba": {"en": "Cordoba", "ru": "Кордова"},
    "Granada": {"en": "Granada", "ru": "Гранада"},
    "Huelva": {"en": "Huelva", "ru": "Уэльва"},
    "Jaén": {"en": "Jaen", "ru": "Хаэн"},
    "Málaga": {"en": "Malaga", "ru": "Малага"},
    "Sevilla": {"en": "Seville", "ru": "Севилья"},
    "Huesca": {"en": "Huesca", "ru": "Уэска"},
    "Teruel": {"en": "Teruel", "ru": "Теруэль"},
    "Zaragoza": {"en": "Zaragoza", "ru": "Сарагоса"},
    "Asturias": {"en": "Asturias", "ru": "Астурия"},
    "Illes Balears": {"en": "Balearic Islands", "ru": "Балеарские острова"},
    "Álava": {"en": "Álava", "ru": "Алава"},
    "Gipuzkoa": {"en": "Gipuzkoa", "ru": "Гипускоа"},
    "Bizkaia": {"en": "Biscay", "ru": "Бискайя"},
    "Las Palmas": {"en": "Las Palmas", "ru": "Лас-Пальмас"},
    "Santa Cruz de Tenerife": {"en": "Santa Cruz de Tenerife", "ru": "Санта-Крус-де-Тенерифе"},
    "Cantabria": {"en": "Cantabria", "ru": "Кантабрия"},
    "Ávila": {"en": "Ávila", "ru": "Авила"},
    "Burgos": {"en": "Burgos", "ru": "Бургос"},
    "León": {"en": "Leon", "ru": "Леон"},
    "Palencia": {"en": "Palencia", "ru": "Паленсия"},
    "Salamanca": {"en": "Salamanca", "ru": "Саламанка"},
    "Segovia": {"en": "Segovia", "ru": "Сеговия"},
    "Soria": {"en": "Soria", "ru": "Сория"},
    "Valladolid": {"en": "Valladolid", "ru": "Вальядолид"},
    "Zamora": {"en": "Zamora", "ru": "Самора"},
    "Albacete": {"en": "Albacete", "ru": "Альбасете"},
    "Ciudad Real": {"en": "Ciudad Real", "ru": "Сьюдад-Реаль"},
    "Cuenca": {"en": "Cuenca", "ru": "Куэнка"},
    "Guadalajara": {"en": "Guadalajara", "ru": "Гвадалахара"},
    "Toledo": {"en": "Toledo", "ru": "Толедо"},
    "Barcelona": {"en": "Barcelona", "ru": "Барселона"},
    "Girona": {"en": "Girona", "ru": "Жирона"},
    "Lleida": {"en": "Lleida", "ru": "Льейда"},
    "Tarragona": {"en": "Tarragona", "ru": "Таррагона"},
    "Alicante": {"en": "Alicante", "ru": "Аликанте"},
    "Castellón": {"en": "Castellon", "ru": "Кастельон"},
    "Valencia": {"en": "Valencia", "ru": "Валенсия"},
    "Badajoz": {"en": "Badajoz", "ru": "Бадахос"},
    "Cáceres": {"en": "Caceres", "ru": "Касерес"},
    "A Coruña": {"en": "A Coruña", "ru": "Ла-Корунья"},
    "Lugo": {"en": "Lugo", "ru": "Луго"},
    "Ourense": {"en": "Ourense", "ru": "Оуренсе"},
    "Pontevedra": {"en": "Pontevedra", "ru": "Понтеведра"},
    "Madrid": {"en": "Madrid", "ru": "Мадрид"},
    "Murcia": {"en": "Murcia", "ru": "Мурсия"},
    "Navarra": {"en": "Navarre", "ru": "Наварра"},
    "La Rioja": {"en": "La Rioja", "ru": "Ла-Риоха"},
    "Ceuta": {"en": "Ceuta", "ru": "Сеута"},
    "Melilla": {"en": "Melilla", "ru": "Мелилья"},
}

MANUAL_TRANSLATIONS: dict[str, dict[str, str]] = {}
MANUAL_TRANSLATIONS.update(BASE_TRANSLATIONS)
MANUAL_TRANSLATIONS.update(AUTONOMY_TRANSLATIONS)
MANUAL_TRANSLATIONS.update(PROVINCE_TRANSLATIONS)

AUTONOMIES = [
    {
        "name": "Andalucía",
        "lat": 37.3891,
        "lon": -5.9845,
        "provinces": [
            "Almería",
            "Cádiz",
            "Córdoba",
            "Granada",
            "Huelva",
            "Jaén",
            "Málaga",
            "Sevilla",
        ],
    },
    {
        "name": "Aragón",
        "lat": 41.6488,
        "lon": -0.8891,
        "provinces": ["Huesca", "Teruel", "Zaragoza"],
    },
    {
        "name": "Principado de Asturias",
        "lat": 43.3614,
        "lon": -5.8494,
        "provinces": ["Asturias"],
    },
    {
        "name": "Illes Balears",
        "lat": 39.6953,
        "lon": 3.0176,
        "provinces": ["Illes Balears"],
    },
    {
        "name": "País Vasco",
        "lat": 43.2627,
        "lon": -2.9253,
        "provinces": ["Álava", "Gipuzkoa", "Bizkaia"],
    },
    {
        "name": "Canarias",
        "lat": 28.2916,
        "lon": -16.6291,
        "provinces": ["Las Palmas", "Santa Cruz de Tenerife"],
    },
    {
        "name": "Cantabria",
        "lat": 43.1828,
        "lon": -3.9878,
        "provinces": ["Cantabria"],
    },
    {
        "name": "Castilla y León",
        "lat": 41.6523,
        "lon": -4.7245,
        "provinces": [
            "Ávila",
            "Burgos",
            "León",
            "Palencia",
            "Salamanca",
            "Segovia",
            "Soria",
            "Valladolid",
            "Zamora",
        ],
    },
    {
        "name": "Castilla-La Mancha",
        "lat": 39.8568,
        "lon": -4.0245,
        "provinces": ["Albacete", "Ciudad Real", "Cuenca", "Guadalajara", "Toledo"],
    },
    {
        "name": "Cataluña",
        "lat": 41.3874,
        "lon": 2.1686,
        "provinces": ["Barcelona", "Girona", "Lleida", "Tarragona"],
    },
    {
        "name": "Comunitat Valenciana",
        "lat": 39.4699,
        "lon": -0.3763,
        "provinces": ["Alicante", "Castellón", "Valencia"],
    },
    {
        "name": "Extremadura",
        "lat": 38.8794,
        "lon": -6.9707,
        "provinces": ["Badajoz", "Cáceres"],
    },
    {
        "name": "Galicia",
        "lat": 42.8805,
        "lon": -8.5457,
        "provinces": ["A Coruña", "Lugo", "Ourense", "Pontevedra"],
    },
    {
        "name": "Comunidad de Madrid",
        "lat": 40.4168,
        "lon": -3.7038,
        "provinces": ["Madrid"],
    },
    {
        "name": "Región de Murcia",
        "lat": 37.9922,
        "lon": -1.1307,
        "provinces": ["Murcia"],
    },
    {
        "name": "Comunidad Foral de Navarra",
        "lat": 42.8125,
        "lon": -1.6458,
        "provinces": ["Navarra"],
    },
    {
        "name": "La Rioja",
        "lat": 42.4627,
        "lon": -2.4449,
        "provinces": ["La Rioja"],
    },
    {
        "name": "Ceuta",
        "lat": 35.8894,
        "lon": -5.3213,
        "provinces": ["Ceuta"],
    },
    {
        "name": "Melilla",
        "lat": 35.2923,
        "lon": -2.9381,
        "provinces": ["Melilla"],
    },
]

MUNICIPALITIES_PER_PROVINCE = 5
SECTORS_PER_MUNICIPALITY = 3


def jitter(lat: float, lon: float, lat_range: float, lon_range: float) -> tuple[float, float]:
    return (
        lat + random.uniform(-lat_range, lat_range),
        lon + random.uniform(-lon_range, lon_range),
    )


def format_point(lat: float, lon: float) -> str:
    return f"({lon:.6f},{lat:.6f})"


def translate_word(word: str, language: str) -> str:
    if language == "es":
        return word

    manual_translations = MANUAL_TRANSLATIONS.get(word)
    if manual_translations and language in manual_translations:
        return manual_translations[language]

    if word.startswith("Municipio "):
        remainder = word[len("Municipio "):]
        index, province_name = remainder.split(" de ", 1)
        province_translation = translate_word(province_name, language)
        if language == "en":
            return f"Municipality {index} of {province_translation}"
        if language == "ru":
            return f"Муниципалитет {index} провинции {province_translation}"

    if word.startswith("Sector "):
        remainder = word[len("Sector "):]
        sector_index, rest = remainder.split(" de Municipio ", 1)
        municipality_index, province_name = rest.split(" de ", 1)
        province_translation = translate_word(province_name, language)
        if language == "en":
            return (
                f"Sector {sector_index} of Municipality {municipality_index} "
                f"of {province_translation}"
            )
        if language == "ru":
            return (
                f"Сектор {sector_index} муниципалитета {municipality_index} "
                f"провинции {province_translation}"
            )

    raise ValueError(f"Missing translation for '{word}' ({language})")


def main() -> None:
    random.seed(42)
    words: set[str] = {"Autonomía", "Provincia", "Municipio", "Sector"}
    locations: list[dict[str, str | None]] = []

    for autonomy in AUTONOMIES:
        autonomy_id = str(uuid.uuid4())
        words.add(autonomy["name"])
        locations.append(
            {
                "id": autonomy_id,
                "name_key": autonomy["name"],
                "type": "Autonomía",
                "parent_id": None,
                "center": format_point(autonomy["lat"], autonomy["lon"]),
            }
        )

        for province_name in autonomy["provinces"]:
            province_id = str(uuid.uuid4())
            words.add(province_name)
            province_lat, province_lon = jitter(autonomy["lat"], autonomy["lon"], 1.5, 1.5)
            locations.append(
                {
                    "id": province_id,
                    "name_key": province_name,
                    "type": "Provincia",
                    "parent_id": autonomy_id,
                    "center": format_point(province_lat, province_lon),
                }
            )

            for municipality_index in range(1, MUNICIPALITIES_PER_PROVINCE + 1):
                municipality_name = f"Municipio {municipality_index} de {province_name}"
                words.add(municipality_name)
                municipality_id = str(uuid.uuid4())
                municipality_lat, municipality_lon = jitter(province_lat, province_lon, 0.4, 0.4)
                locations.append(
                    {
                        "id": municipality_id,
                        "name_key": municipality_name,
                        "type": "Municipio",
                        "parent_id": province_id,
                        "center": format_point(municipality_lat, municipality_lon),
                    }
                )

                for sector_index in range(1, SECTORS_PER_MUNICIPALITY + 1):
                    sector_name = f"Sector {sector_index} de {municipality_name}"
                    words.add(sector_name)
                    sector_id = str(uuid.uuid4())
                    sector_lat, sector_lon = jitter(municipality_lat, municipality_lon, 0.05, 0.05)
                    locations.append(
                        {
                            "id": sector_id,
                            "name_key": sector_name,
                            "type": "Sector",
                            "parent_id": municipality_id,
                            "center": format_point(sector_lat, sector_lon),
                        }
                    )

    words_path = "src/main/resources/liquibase/data/word-location.csv"
    locations_path = "src/main/resources/liquibase/data/locations.csv"
    translations_path = "src/main/resources/liquibase/data/translations.csv"
    languages_path = "src/main/resources/liquibase/data/languages.csv"

    with open(words_path, "w", encoding="utf-8", newline="") as words_file:
        writer = csv.writer(words_file)
        writer.writerow(["key"])
        for value in sorted(words):
            writer.writerow([value])

    with open(locations_path, "w", encoding="utf-8", newline="") as locations_file:
        writer = csv.DictWriter(
            locations_file,
            fieldnames=["id", "name_key", "type", "parent_id", "center"],
        )
        writer.writeheader()
        for row in locations:
            writer.writerow(row)

    translations: list[dict[str, str]] = []
    for word in sorted(words):
        for language in LANGUAGE_CODES_FOR_TRANSLATIONS:
            translations.append(
                {
                    "word_key": word,
                    "language_key": language,
                    "value": translate_word(word, language),
                }
            )

    with open(translations_path, "w", encoding="utf-8", newline="") as translations_file:
        writer = csv.DictWriter(
            translations_file,
            fieldnames=["word_key", "language_key", "value"],
        )
        writer.writeheader()
        for row in translations:
            writer.writerow(row)

    with open(languages_path, "w", encoding="utf-8", newline="") as languages_file:
        writer = csv.writer(languages_file)
        writer.writerow(["key"])
        for code in LANGUAGE_CODES_FOR_TABLE:
            writer.writerow([code])


if __name__ == "__main__":
    main()
