import { createI18n } from "vue-i18n";

import en from "../assets/locale/EN-en";
const locales = {
  pl: "../assets/locale/PL-pl",
  es: "../assets/locale/ES-es",
  en: "../assets/locale/EN-en",
};
async function getMessages(localeCode) {
  if (localeCode == "en" || localeCode in locales) {
    return { en: en };
  } else {
    var locale = await import(locales[localeCode]);
    return { localeCode: locale, en: en };
  }
}

const getNavigatorLanguage = () => {
  if (navigator.languages && navigator.languages.length) {
    return navigator.languages[0].split("-")[0].toLowerCase();
  } else {
    return navigator.userLanguage || navigator.language || navigator.browserLanguage || "en";
  }
};
var localeCode = "en";
try {
  localeCode = getNavigatorLanguage();
} catch (error) {
  console.error(error);
}

// const messages = { pl: pl, en: en };
if (!(localeCode in locales)) {
  localeCode = "en";
}
const messages = await getMessages(localeCode);
const i18n = createI18n({
  locale: localeCode,
  defaultLocale: "en",
  messages,
});

export default i18n;
