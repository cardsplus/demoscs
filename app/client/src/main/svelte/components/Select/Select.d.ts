import { SvelteTypedComponent , SvelteAllProps } from 'svelte-typed-component';

export default class Select extends SvelteTypedComponent<SelectProps, SelectEvents, SelectSlots> {
}

declare const _SelectProps: {

    /** 
     * Disabled state.
     */
    disabled?: boolean;

    /** 
     * Input label
     */
    label?: string;

    /** 
     * Array of Items.
     */
    items?: ({value:string, text: string}|string)[];

    /** 
     * Selected Value.
     */
    value?: string;
    
} & SvelteAllProps;

declare const _SelectEvents: {
    click: any;
    change: any;
};

declare const _SelectSlots: {
};

export declare type SelectProps = typeof _SelectProps;
export declare type SelectEvents = typeof _SelectEvents;
export declare type SelectSlots = typeof _SelectSlots;
export {};
