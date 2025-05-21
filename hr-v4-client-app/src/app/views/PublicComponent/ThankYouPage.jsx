import React, { useEffect, useState } from 'react';
import { Box, Card, CardContent, Container, Typography, Button } from "@material-ui/core";
import CheckCircleOutlineIcon from "@material-ui/icons/CheckCircleOutline";
import { makeStyles } from '@material-ui/core/styles';

const useStyles = makeStyles((theme) => ({
    page: {
        minHeight: '100vh',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
        textAlign: 'center',
        background: 'linear-gradient(to right, #e0f7fa, #f1f8e9)',
    },
    card: {
        padding: theme.spacing(4),
        borderRadius: theme.spacing(3),
        boxShadow: '0 8px 20px rgba(0,0,0,0.1)',
        maxWidth: 500,
        animation: '$fadeIn 1s ease-out',
    },
    icon: {
        fontSize: 100,
        color: theme.palette.success.main,
        marginBottom: theme.spacing(2),
    },
    button: {
        marginTop: theme.spacing(3),
        borderRadius: 50,
        paddingLeft: theme.spacing(4),
        paddingRight: theme.spacing(4),
    },
    '@keyframes fadeIn': {
        from: { opacity: 0, transform: 'translateY(20px)' },
        to: { opacity: 1, transform: 'translateY(0)' },
    }
}));

const ThankYouPage = () => {
    const classes = useStyles();
    return (
        <Box className={classes.page}>
            <Card className={classes.card}>
                <CardContent>
                    <CheckCircleOutlineIcon className={classes.icon} />
                    <Typography variant="h4" gutterBottom>
                        Cảm ơn bạn!
                    </Typography>
                    <Typography variant="body1">
                        Chúng tôi đã nhận được thông tin của bạn.<br />
                    </Typography>
                </CardContent>
            </Card>
        </Box>
    );
};

export default ThankYouPage;
