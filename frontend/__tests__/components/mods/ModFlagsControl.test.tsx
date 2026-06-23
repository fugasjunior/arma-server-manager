import React from 'react';
import '@testing-library/jest-dom';
import {render, screen, fireEvent} from '@testing-library/react';
import ModFlagsControl from '../../../src/components/mods/ModFlagsControl';
import {ServerType} from '../../../src/api/generated';

jest.mock('../../../src/config', () => ({
    __esModule: true,
    default: {apiUrl: '/api', dateFormat: {}, version: 'test'},
}));

const allOn = {loadOnClient: true, loadOnServer: true, loadOnHeadlessClient: true};
const allOff = {loadOnClient: false, loadOnServer: false, loadOnHeadlessClient: false};
const serverOnly = {loadOnClient: false, loadOnServer: true, loadOnHeadlessClient: false};
const clientOnly = {loadOnClient: true, loadOnServer: false, loadOnHeadlessClient: false};

describe('ModFlagsControl', () => {
    it('shows "Server + Client" when both flags on', () => {
        render(<ModFlagsControl flags={allOn} serverType={ServerType.Arma3} disabled={false} onChange={jest.fn()}/>);
        expect(screen.getByText('Server + Client')).toBeInTheDocument();
    });

    it('shows "Server only" for server-only flags', () => {
        render(<ModFlagsControl flags={serverOnly} serverType={ServerType.Arma3} disabled={false} onChange={jest.fn()}/>);
        expect(screen.getByText('Server only')).toBeInTheDocument();
    });

    it('shows "Client only" for client-only flags', () => {
        render(<ModFlagsControl flags={clientOnly} serverType={ServerType.Arma3} disabled={false} onChange={jest.fn()}/>);
        expect(screen.getByText('Client only')).toBeInTheDocument();
    });

    it('shows HC checkbox for Arma3', () => {
        render(<ModFlagsControl flags={allOn} serverType={ServerType.Arma3} disabled={false} onChange={jest.fn()}/>);
        expect(screen.getByLabelText('HC')).toBeInTheDocument();
    });

    it('hides HC checkbox for DayZ', () => {
        render(<ModFlagsControl flags={allOn} serverType={ServerType.Dayz} disabled={false} onChange={jest.fn()}/>);
        expect(screen.queryByLabelText('HC')).not.toBeInTheDocument();
    });

    it('toggling HC calls onChange preserving role flags', () => {
        const onChange = jest.fn();
        render(<ModFlagsControl flags={serverOnly} serverType={ServerType.Arma3} disabled={false} onChange={onChange}/>);
        fireEvent.click(screen.getByLabelText('HC'));
        expect(onChange).toHaveBeenCalledWith({loadOnClient: false, loadOnServer: true, loadOnHeadlessClient: true});
    });

    it('select is aria-disabled when disabled prop is true', () => {
        render(<ModFlagsControl flags={allOn} serverType={ServerType.Arma3} disabled={true} onChange={jest.fn()}/>);
        expect(screen.getByRole('combobox')).toHaveAttribute('aria-disabled', 'true');
    });

    it('HC checkbox is disabled when disabled prop is true', () => {
        render(<ModFlagsControl flags={allOn} serverType={ServerType.Arma3} disabled={true} onChange={jest.fn()}/>);
        expect(screen.getByLabelText('HC')).toBeDisabled();
    });

    it('HC checkbox is disabled and checked when role is Server + Client', () => {
        render(<ModFlagsControl flags={{...allOn, loadOnHeadlessClient: false}} serverType={ServerType.Arma3} disabled={false} onChange={jest.fn()}/>);
        const hc = screen.getByLabelText('HC');
        expect(hc).toBeDisabled();
        expect(hc).toBeChecked();
    });

    it('HC checkbox is enabled when role is Server only', () => {
        render(<ModFlagsControl flags={serverOnly} serverType={ServerType.Arma3} disabled={false} onChange={jest.fn()}/>);
        expect(screen.getByLabelText('HC')).not.toBeDisabled();
    });

    it('renders empty placeholder for all-off flags', () => {
        render(<ModFlagsControl flags={allOff} serverType={ServerType.Arma3} disabled={false} onChange={jest.fn()}/>);
        // No role option text visible; select shows the em-dash placeholder
        expect(screen.queryByText('Server + Client')).not.toBeInTheDocument();
        expect(screen.queryByText('Server only')).not.toBeInTheDocument();
        expect(screen.queryByText('Client only')).not.toBeInTheDocument();
    });
});
