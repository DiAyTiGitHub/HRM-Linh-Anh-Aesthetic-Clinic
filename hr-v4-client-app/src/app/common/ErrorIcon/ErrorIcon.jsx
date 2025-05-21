import React from 'react';
import Icon from '@material-ui/icons/ErrorOutline';
import styles from "./styles.module.scss";
import { Tooltip } from '@material-ui/core';

const ErrorIcon = ({helperText}) => {
    return (
        <Tooltip arrow title={helperText}>
            <Icon color="error" className={styles.icon}/>
        </Tooltip>
    )
}

export default React.memo(ErrorIcon);