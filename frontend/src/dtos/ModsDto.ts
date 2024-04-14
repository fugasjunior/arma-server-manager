import {ModDto} from "./ModDto.ts";
import {CreatorDlcDto} from "./CreatorDlcDto.ts";

export interface ModsDto {
    workshopMods: Array<ModDto>,
    creatorDlcs: Array<CreatorDlcDto>
}
