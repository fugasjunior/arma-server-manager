import {Button, Grid, Stack, Typography, useTheme} from "@mui/material";
import logoBlack from "../img/asm_logo_black.png";
import logoWhite from "../img/asm_logo.png";
import {DiscordIcon} from "../icons/DiscordIcon.tsx";
import {GitHubIcon} from "../icons/GitHubIcon.tsx";
import {BuyMeACoffeeIcon} from "../icons/BuyMeACoffeeIcon.tsx";
import {PayPalIcon} from "../icons/PayPalIcon.tsx";
import config from "../config.ts";

const AboutPage = () => {
    const theme = useTheme();
    const logo = theme.palette.mode === "dark" ? logoWhite : logoBlack;

    return (
        <Grid container sx={{justifyContent: "center"}}>
            <Stack sx={{alignItems: "center"}}>
                <img
                    alt="Arma Server Manager Logo"
                    title="Arma Server Manager"
                    src={logo}
                    style={{height: 100}}
                />
                <Typography variant="h4" sx={{mt: 1}}>Arma Server Manager</Typography>
                <Typography variant="subtitle2">{config.version}</Typography>
                <Typography variant="subtitle1">by fugasjunior</Typography>
                <Stack direction="row" spacing={2} sx={{mt: 2}}>
                    <Button target="_blank" href="https://github.com/fugasjunior/arma-server-manager"
                            variant="contained"
                            startIcon={<GitHubIcon/>}
                    >
                        GitHub
                    </Button>
                    <Button target="_blank" href="https://discord.gg/Yn93vCADPg"
                            variant="contained"
                            startIcon={<DiscordIcon/>}
                    >
                        Discord
                    </Button>
                </Stack>
                <Typography variant="h6" sx={{mt: 2}}>Support</Typography>
                <Stack direction="row" spacing={3}>
                    <Button target="_blank" href="https://www.buymeacoffee.com/fugasjunior"
                            variant="contained"
                            startIcon={<BuyMeACoffeeIcon/>}
                            sx={{bgcolor: "#FFDD00", color: "#000", "&:hover": {bgcolor: "#e5c700"}}}
                    >
                        Buy Me A Coffee
                    </Button>
                    <Button target="_blank" href="https://paypal.me/fugasjunior"
                            variant="contained"
                            startIcon={<PayPalIcon/>}
                            sx={{bgcolor: "#0070BA", color: "#fff", "&:hover": {bgcolor: "#005c99"}}}
                    >
                        Donate with PayPal
                    </Button>
                </Stack>
            </Stack>
        </Grid>
    )
}

export default AboutPage;