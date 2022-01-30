import { createValue } from './rest.js';
import { updateValue } from './rest.js';
export async function createTestSet() {
    try {
        await Promise.all(allSprache.map(value => {
              return createValue("/api/enum/sprache/", value);
        }));
    } catch (err) {
        // just ignore
    }
    await Promise.all(allNutzer.map(value => {
		  return updateValue("/api/nutzer/" + value.id, value);
    }));
    await Promise.all(allProjekt.map(value => {
		  return updateValue("/api/projekt/" + value.id, value);
    }));
    await Promise.all(allAufgabe.map(value => {
		  return updateValue("/api/aufgabe/" + value.id, value);
    }));
}

import { removeValue } from './rest.js';
export async function removeTestSet() {
    await Promise.all(allAufgabe.map(value => {
		  return removeValue("/api/aufgabe/" + value.id);
    }));
    await Promise.all(allProjekt.map(value => {
		  return removeValue("/api/projekt/" + value.id);
    }));
    await Promise.all(allNutzer.map(value => {
		  return removeValue("/api/nutzer/" + value.id);
    }));
}

const allSprache = [
    {code: 0, name: 'DE', text: 'Deutsch'},
    {code: 1, name: 'EN', text: 'Englisch'},
    {code: 2, name: 'IT', text: 'Italienisch'},
    {code: 3, name: 'FR', text: 'Französisch'}
]

const allNutzer = [
    {
        id: '00000001-2222-2222-2222-222222222222',
        mail: 'robert.bruckbauer@e-mundo.de',
        name: 'Robert Bruckbauer',
        aktiv: true,
        allSprache: ['EN','IT']
    },
    {
        id: '00000002-2222-2222-2222-222222222222',
        mail: 'bruckbauer@gmx.at',
        name: 'Robert Bruckbauer',
        aktiv: true,
        allSprache: ['EN']
    },
    {
        id: '00000003-2222-2222-2222-222222222222',
        mail: 'bromertje@gmail.com',
        name: 'Robert Bruckbauer',
        aktiv: false,
        allSprache: ['DE']
    }
];

const allProjekt = [
    {
        id: '00000001-3333-3333-3333-333333333333',
        name: 'Projekt Alpha',
        aktiv: true,
        sprache: 'DE',
        besitzer: '/api/nutzer/00000001-2222-2222-2222-222222222222',
        allMitglied: [
            '/api/nutzer/00000001-2222-2222-2222-222222222222'
        ]
    },
    {
        id: '00000002-3333-3333-3333-333333333333',
        name: 'Projekt Beta',
        aktiv: true,
        sprache: 'DE',
        besitzer: '/api/nutzer/00000001-2222-2222-2222-222222222222',
        allMitglied: [
            '/api/nutzer/00000001-2222-2222-2222-222222222222',
            '/api/nutzer/00000002-2222-2222-2222-222222222222',
            '/api/nutzer/00000003-2222-2222-2222-222222222222'
        ]
    },
    {
        id: '00000003-3333-3333-3333-333333333333',
        name: 'Projekt Gamma',
        aktiv: false,
        sprache: 'EN',
        besitzer: null,
        allMitglied: [
        ]
    }
];

const allAufgabe = [
    {
        id: '00000001-4444-4444-4444-444444444444',
        text: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.',
        aktiv: true,
        projekt: '/api/projekt/00000001-3333-3333-3333-333333333333'
    },
    {
        id: '00000002-4444-4444-4444-444444444444',
        text: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.',
        aktiv: false,
        projekt: '/api/projekt/00000002-3333-3333-3333-333333333333'
    },
    {
        id: '00000003-4444-4444-4444-444444444444',
        text: 'At vero eos et accusam et justo duo dolores et ea rebum.',
        aktiv: true,
        projekt: '/api/projekt/00000001-3333-3333-3333-333333333333'
    },
    {
        id: '00000004-4444-4444-4444-444444444444',
        text: 'Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.',
        aktiv: true,
        projekt: '/api/projekt/00000001-3333-3333-3333-333333333333'
    },
    {
        id: '00000005-4444-4444-4444-444444444444',
        text: "Magni accusantium labore et id quis provident.",
        aktiv: true,
        projekt: '/api/projekt/00000001-3333-3333-3333-333333333333'
      },
      {
        id: '00000006-4444-4444-4444-444444444444',
        text: "Consectetur impedit quisquam qui deserunt non rerum consequuntur eius.",
        aktiv: true,
        projekt: '/api/projekt/00000001-3333-3333-3333-333333333333'
      },
      {
        id: '00000007-4444-4444-4444-444444444444',
        text: "Quia atque aliquam sunt impedit voluptatum rerum assumenda nisi.",
        aktiv: true,
        projekt: '/api/projekt/00000001-3333-3333-3333-333333333333'
      },
      {
        id: '0000000a-4444-4444-4444-444444444444',
        text: "Cupiditate quos possimus corporis quisquam exercitationem beatae.",
        aktiv: true,
        projekt: '/api/projekt/00000001-3333-3333-3333-333333333333'
      }
]
