import { Button, Collapse, Grid } from "@material-ui/core";
import { observer } from "mobx-react";
import React, { memo, useEffect, useState } from "react";
import FilterListIcon from '@material-ui/icons/FilterList';
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import { Form, Formik, useFormikContext } from "formik";
import { FilterOptionKanban } from "app/LocalConstants";
import CheckIcon from '@material-ui/icons/Check';
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { useStore } from "app/stores";
import GlobitsAutocomplete from "app/common/form/GlobitsAutocomplete";
import { useTranslation } from "react-i18next";
import { makeStyles } from '@material-ui/core/styles';
import Accordion from '@material-ui/core/Accordion';
import AccordionDetails from '@material-ui/core/AccordionDetails';
import AccordionSummary from '@material-ui/core/AccordionSummary';
import Typography from '@material-ui/core/Typography';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";

function FilterAccordion({ children, title, component, openAtFirst = true }) {

    const [expanded, setExpanded] = useState(openAtFirst);

    return (
        <Accordion
            component="section"
            expanded={expanded}
            onChange={(_, value) => setExpanded(value)}
            className="card accordion-root"
        >
            <AccordionSummary>
                <svg className="accordion-icon" fill="#000000" width="24px" height="24px" viewBox="0 0 24 24" enableBackground="new 0 0 24 24">
                    <g strokeWidth="0"></g>
                    <g strokeLinecap="round" strokeLinejoin="round"></g>
                    <g>
                        <path d="M9.9,17.2c-0.6,0-1-0.4-1-1c0-0.3,0.1-0.5,0.3-0.7l3.5-3.5L9.2,8.5c-0.4-0.4-0.4-1,0-1.4c0.4-0.4,1-0.4,1.4,0l4.2,4.2c0.4,0.4,0.4,1,0,1.4c0,0,0,0,0,0l-4.2,4.2C10.4,17.1,10.1,17.2,9.9,17.2z"></path>
                    </g>
                </svg>
                <p className="accordion-title">{title}</p>
            </AccordionSummary>

            <AccordionDetails className="p-8">
                {children ? children : component ? component : ''}
            </AccordionDetails>
        </Accordion>
    )
}

export default memo(observer(FilterAccordion));
