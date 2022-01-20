import { SvelteTypedComponent , SvelteAllProps } from 'svelte-typed-component';
export default class CheckboxGroup extends SvelteTypedComponent<CheckboxGroupProps, CheckboxGroupEvents, CheckboxGroupSlots> {
}

declare const _CheckboxGroupProps: {
    
    /** 
     * Disabled state.
     */
    disabled?: boolean;

    /** 
     * Group values.
     */
    group?: string[];

    /** 
     * Input label
     */
    label?: string;

    /** 
     * Input value.
    */
    value?: string;

} & SvelteAllProps;

declare const _CheckboxGroupEvents: {
    change: Event;
};

declare const _CheckboxGroupSlots: {
};

export declare type CheckboxGroupProps = typeof _CheckboxGroupProps;
export declare type CheckboxGroupEvents = typeof _CheckboxGroupEvents;
export declare type CheckboxGroupSlots = typeof _CheckboxGroupSlots;
export {};
