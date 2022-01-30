export function toNutzerItem(nutzer) {
    return {
        value: nutzer.id,
        text: nutzer.name + " <" + nutzer.mail + ">"
    };
}

export function toNutzerLink(nutzer) {
    if (nutzer.id) {
        return '/api/nutzer/' + nutzer.id;
    } else {
        return '/api/nutzer/' + nutzer.value;
    }
}
