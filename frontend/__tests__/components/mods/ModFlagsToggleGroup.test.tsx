import React from 'react';
import '@testing-library/jest-dom';
import {render, screen, fireEvent} from '@testing-library/react';
import ModFlagsToggleGroup from '../../../src/components/mods/ModFlagsToggleGroup';
import {ServerType} from '../../../src/api/generated';

jest.mock('../../../src/config', () => ({
    __esModule: true,
    default: {apiUrl: '/api', dateFormat: {}, version: 'test'},
}));

const allOn = {loadOnClient: true, loadOnServer: true, loadOnHeadlessClient: true};
const allOff = {loadOnClient: false, loadOnServer: false, loadOnHeadlessClient: false};

describe('ModFlagsToggleGroup', () => {
    it('renders C and S buttons for DayZ (no H)', () => {
        render(<ModFlagsToggleGroup flags={allOn} serverType={ServerType.Dayz} disabled={false} onChange={jest.fn()}/>);
        expect(screen.getByText('C')).toBeInTheDocument();
        expect(screen.getByText('S')).toBeInTheDocument();
        expect(screen.queryByText('H')).not.toBeInTheDocument();
    });

    it('renders H button for Arma3', () => {
        render(<ModFlagsToggleGroup flags={allOn} serverType={ServerType.Arma3} disabled={false} onChange={jest.fn()}/>);
        expect(screen.getByText('H')).toBeInTheDocument();
    });

    it('calls onChange with correct flags when C toggled off', () => {
        const onChange = jest.fn();
        render(<ModFlagsToggleGroup flags={allOn} serverType={ServerType.Arma3} disabled={false} onChange={onChange}/>);
        fireEvent.click(screen.getByText('C'));
        expect(onChange).toHaveBeenCalledWith({loadOnClient: false, loadOnServer: true, loadOnHeadlessClient: true});
    });

    it('calls onChange with correct flags when S toggled on', () => {
        const onChange = jest.fn();
        render(<ModFlagsToggleGroup flags={allOff} serverType={ServerType.Arma3} disabled={false} onChange={onChange}/>);
        fireEvent.click(screen.getByText('S'));
        expect(onChange).toHaveBeenCalledWith({loadOnClient: false, loadOnServer: true, loadOnHeadlessClient: false});
    });

    it('does not call onChange when disabled', () => {
        const onChange = jest.fn();
        render(<ModFlagsToggleGroup flags={allOn} serverType={ServerType.Arma3} disabled={true} onChange={onChange}/>);
        fireEvent.click(screen.getByText('C'));
        expect(onChange).not.toHaveBeenCalled();
    });
});
