import { writable } from 'svelte/store';
const createdStorage = localStorage.getItem("created");
export const created = writable(createdStorage);
created.subscribe(value => {
    localStorage.setItem("created", value === true ? true : false);
});

import { updateValue, removeValue } from './rest.js';
export async function createTestSet() {
    const allNutzerRes = await Promise.all(allNutzer.map(value => {
		return updateValue("/api/nutzer/" + value.dataId, value);
    }));
    const allProjektRes = await Promise.all(allProjekt.map(value => {
		return updateValue("/api/projekt/" + value.dataId, value);
    }));    
    const allAufgabeRes = await Promise.all(allAufgabe.map(value => {
		return updateValue("/api/aufgabe/" + value.dataId, value);
    }));
    created.set(true);
    return allNutzerRes + allProjektRes + allAufgabeRes;
}
export async function removeTestSet() {
    const allProjektRes = await Promise.all(allProjekt.map(value => {
		return removeValue("/api/projekt/" + value.dataId);
    }));
    const allNutzerRes = await Promise.all(allNutzer.map(value => {
		 return removeValue("/api/nutzer/" + value.dataId);
    }));
    created.set(false);
    return allNutzerRes + allProjektRes;
}

const allNutzer = [
    {
        dataId: '00000001-2222-2222-2222-222222222222',
        mail: 'robert.bruckbauer@e-mundo.de',
        name: 'Robert Bruckbauer',
        aktiv: true,
        allRolle: ['VERWALTER']
    },
    {
        dataId: '00000002-2222-2222-2222-222222222222',
        mail: 'bruckbauer@gmx.at',
        name: 'Robert Bruckbauer',
        aktiv: true,
        allRolle: ['BEARBEITER']
    },
    {
        dataId: '00000003-2222-2222-2222-222222222222',
        mail: 'bromertje@gmail.com',
        name: 'Robert Bruckbauer',
        aktiv: true,
        allRolle: ['BESUCHER']
    }
];

const allProjekt = [
    {
        dataId: '00000001-3333-3333-3333-333333333333',
        name: 'Projekt Alpha',
        aktiv: true,
        besitzer: {refId: '00000001-2222-2222-2222-222222222222'}
    },
    {
        dataId: '00000002-3333-3333-3333-333333333333',
        name: 'Projekt Beta',
        aktiv: true,
        besitzer: {refId: '00000001-2222-2222-2222-222222222222'}
    },
    {
        dataId: '00000003-3333-3333-3333-333333333333',
        name: 'Projekt Gamma',
        aktiv: false,
        besitzer: {refId: '00000001-2222-2222-2222-222222222222'}
    }
];

const allAufgabe = [
    {
        dataId: '00000001-4444-4444-4444-444444444444',
        text: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.',
        aktiv: true,
        projekt: '/api/projekt/00000001-3333-3333-3333-333333333333'
    },
    {
        dataId: '00000002-4444-4444-4444-444444444444',
        text: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.',
        aktiv: true,
        projekt: '/api/projekt/00000002-3333-3333-3333-333333333333'
    },
    {
        dataId: '00000003-4444-4444-4444-444444444444',
        text: 'At vero eos et accusam et justo duo dolores et ea rebum.',
        aktiv: true,
        projekt: '/api/projekt/00000001-3333-3333-3333-333333333333'
    },
    {
        dataId: '00000004-4444-4444-4444-444444444444',
        text: 'Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.',
        aktiv: true,
        projekt: '/api/projekt/00000001-3333-3333-3333-333333333333'
    },
    {
        dataId: '00000005-4444-4444-4444-444444444444',
        text: "magni accusantium labore et id quis provident.",
        aktiv: true,
        projekt: '/api/projekt/00000001-3333-3333-3333-333333333333'
      },
      {
        dataId: '00000006-4444-4444-4444-444444444444',
        text: "consectetur impedit quisquam qui deserunt non rerum consequuntur eius.",
        aktiv: true,
        projekt: '/api/projekt/00000001-3333-3333-3333-333333333333'
      },
      {
        dataId: '00000007-4444-4444-4444-444444444444',
        text: "quia atque aliquam sunt impedit voluptatum rerum assumenda nisi.",
        aktiv: true,
        projekt: '/api/projekt/00000001-3333-3333-3333-333333333333'
      },
      {
        dataId: '0000000a-4444-4444-4444-444444444444',
        text: "cupiditate quos possimus corporis quisquam exercitationem beatae.",
        aktiv: true,
        projekt: '/api/projekt/00000001-3333-3333-3333-333333333333'
      }
]
