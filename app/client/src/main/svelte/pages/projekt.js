export function toProjektItem(projekt) {
    return {
        value: projekt.id,
        text: projekt.name
    };
}

export function toProjektLink(projekt) {
    if (projekt.id) {
        return '/api/projekt/' + projekt.id;
    } else {
        return '/api/projekt/' + projekt.value;
    }
}
