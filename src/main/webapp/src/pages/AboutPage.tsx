import {Button, Grid, Stack, Typography} from "@mui/material";
import logo from "../img/asm_logo_black.png";
import {DiscordIcon} from "../icons/DiscordIcon.tsx";
import {GitHubIcon} from "../icons/GitHubIcon.tsx";
import {BuyMeACoffeeIcon} from "../icons/BuyMeACoffeeIcon.tsx";
import {PayPalIcon} from "../icons/PayPalIcon.tsx";
import config from "../config.ts";

const AboutPage = () => {

    return (
        <Grid container justifyContent="center">
            <Stack alignItems="center">
                <img
                    alt="Arma Server Manager Logo"
                    title="Arma Server Manager"
                    src={logo}
                    style={{height: 100}}
                />
                <Typography variant="h4" mt={1}>Arma Server Manager</Typography>
                <Typography variant="subtitle2">{config.version}</Typography>
                <Typography variant="subtitle1">by fugasjunior</Typography>
                <Stack direction="row" mt={2} spacing={2}>
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
                <Typography variant="h6" mt={2}>Support</Typography>
                <Stack direction="row" spacing={3}>
                    <Button target="_blank" href="https://www.buymeacoffee.com/fugasjunior"
                            startIcon={<BuyMeACoffeeIcon/>}
                    >
                        Buy Me A Coffee
                    </Button>
                    <Button target="_blank" href="https://paypal.me/fugasjunior"
                            startIcon={<PayPalIcon/>}
                    >
                        Donate with PayPal
                    </Button>
                </Stack>
            </Stack>
        </Grid>
    )
}

export default AboutPage;