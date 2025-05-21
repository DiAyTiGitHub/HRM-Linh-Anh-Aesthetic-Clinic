import React from 'react';
import {
    makeStyles,
    DialogActions,
    Button,
    DialogContent} from '@material-ui/core';
import SaveIcon from '@material-ui/icons/Save';
import BlockIcon from '@material-ui/icons/Block';
import { useTranslation } from 'react-i18next';
import { useFormik } from 'formik';
import * as Yup from 'yup';
import { Link } from "react-router-dom";

const useStyles = makeStyles((theme) => ({
    root: {
        // padding: '10px 0'
    },
    gridItem: {
        margin: '10px 0 !important'
    },
    textField: {
        backgroundColor: '#fff',
        width: '100%',
    },
}));
export default function UnitForm(props) {

    const classes = useStyles();

    const {
        initialValues,
        handleSubmit,
    } = props;
    const { t } = useTranslation();

    const validationSchema = Yup.object({
        code: Yup.string().required(),
        name: Yup.string().required(),
        level: Yup.string().required(),
    });

    const formik = useFormik({
        enableReinitialize: true,
        initialValues,
        validationSchema: validationSchema,
        onSubmit: values => {
            handleSubmit(values);
        }
    });

    return (
      <div className={classes.root}>
        <form onSubmit={formik.handleSubmit}>
          <div className="dialog-body">
            <DialogContent className="o-hidden">
            </DialogContent>
          </div>
          <div className="dialog-footer">
            <DialogActions className="p-0">
              <div className="flex flex-space-between flex-middle">
                <Link to='/dashboard/health_org'>
                    <Button
                    startIcon={<BlockIcon />}
                    variant="contained"
                    className="mr-12 btn btn-secondary d-inline-flex"
                    >
                        {t("general.button.cancel")}
                    </Button>
                </Link>
                <Button
                  startIcon={<SaveIcon />}
                  className="mr-0 btn btn-primary d-inline-flex"
                  variant="contained"
                  color="primary"
                  type="submit"
                >
                  {t("general.button.save")}
                </Button>
              </div>
            </DialogActions>
          </div>
        </form>
      </div>
    );
}