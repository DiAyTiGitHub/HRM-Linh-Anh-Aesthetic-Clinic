import { Box, LinearProgress, Typography, Tooltip } from '@material-ui/core'
import React, { memo } from 'react'
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';

function LinearProgressWithLabel(props) {
    
    return (
        <Tooltip placement='top' title='Tiến độ hoàn thành'>
            <Box display="flex" alignItems="center" className='pt-8 linearProgressWrapper'>
                <Box minWidth={35}>
                    <Typography variant="body2" color="textSecondary" className='pr-6'>{`${Math.round(props.value,)}%`}</Typography>
                </Box>
                <Box width="100%">
                    <LinearProgress
                        variant="determinate" {...props}
                        className={`${Math.round(props?.value,) === 100 ? "linecomplete" : "line"}  bgc-green-aqua`}
                    />
                </Box>
            </Box>
        </Tooltip>
    );
}

LinearProgressWithLabel.propTypes = {
    value: PropTypes.number.isRequired,
};


export default memo(observer(LinearProgressWithLabel));