import React, {useEffect, useState} from "react";
import {Form, Formik, useFormikContext} from "formik";
import {Button, DialogActions, DialogContent, Grid} from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import {useTranslation} from "react-i18next";
import {useStore} from "../../stores";
import * as Yup from "yup";
import {observer} from "mobx-react";

import GlobitsTextField from "../../common/form/GlobitsTextField";
import GlobitsPopupV2 from "../../common/GlobitsPopupV2";
import LocalConstants from "../../LocalConstants";
import {getAllByLevel} from "./AdministrativeUnitService";
import GlobitsSelectInput from "../../common/form/GlobitsSelectInput";
import GlobitsAutocomplete from "../../common/form/GlobitsAutocomplete";

export default observer(function AdministrativeUnitForm(props) {
    const {administrativeUnitStore} = useStore();
    const {t} = useTranslation();

    const {
        handleClose,
        createAdministrative,
        editAdministrative,
        selectedAdministrativeUnit,
        preHandleSubmitData,
        intactAdministrative,
        shouldOpenEditorDialog
    } = administrativeUnitStore;

    const [administrative, setAdministrative] = useState(intactAdministrative);

    const validationSchema = Yup.object({
        code: Yup.string()
            .required(t("validation.required"))
            .matches(LocalConstants.REGEX_SPECIAL_CHARACTERS, t("invalidSign.code")), name: Yup.string()
            .required(t("validation.required"))
            .matches(LocalConstants.REGEX_SPECIAL_CHARACTERS, t("invalidSign.name")), description: Yup.string()
            .nullable()
            .matches(LocalConstants.REGEX_SPECIAL_CHARACTERS, t("invalidSign.description")),
    });

    useEffect(() => {
        if (selectedAdministrativeUnit) setAdministrative(selectedAdministrativeUnit); else setAdministrative(intactAdministrative)
    }, [selectedAdministrativeUnit]);

    function hanledFormSubmit(values) {
        const data = preHandleSubmitData(values);
        if (data.id.length === 0) {
            createAdministrative(data);
        } else {
            editAdministrative(data);
        }
    }

    return (<GlobitsPopupV2
        size={"sm"}
        open={shouldOpenEditorDialog}
        noDialogContent
        title={(administrative?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + t("administrativeUnit.title")}
        onClosePopup={handleClose}
    >
        <Formik
            validationSchema={validationSchema}
            initialValues={administrative}
            enableReinitialize
            onSubmit={(values) => hanledFormSubmit(values)}
        >
            {({isSubmitting}) => (<Form autoComplete="off">
                <div className="dialog-body">
                    <DialogContent className="o-hidden">
                        <Grid container spacing={2}>
                            <Grid item md={12} sm={12} xs={12}>
                                <GlobitsTextField
                                    label={t("country.code")}
                                    name="code"
                                    required
                                />
                            </Grid>
                            <Grid item md={12} sm={12} xs={12}>
                                <GlobitsTextField
                                    label={t("country.name")}
                                    name="name"
                                    required
                                />
                            </Grid>
                            <SelectLevel t={t}/>
                            <Grid item md={12} sm={12} xs={12}>
                                <GlobitsTextField
                                    label={t("country.description")}
                                    name="description"
                                    multiline
                                    rows={3}
                                />
                            </Grid>
                        </Grid>
                    </DialogContent>
                </div>
                <div className="dialog-footer">
                    <DialogActions className="p-0">
                        <div className="flex flex-space-between flex-middle">
                            <Button
                                startIcon={<BlockIcon/>}
                                variant="contained"
                                className="mr-12 btn btn-secondary d-inline-flex"
                                color="secondary"
                                onClick={() => handleClose()}
                            >
                                {t("general.button.cancel")}
                            </Button>
                            <Button
                                startIcon={<SaveIcon/>}
                                className="mr-0 btn btn-primary d-inline-flex"
                                variant="contained"
                                color="primary"
                                type="submit"
                                disabled={isSubmitting}
                            >
                                {t("general.button.save")}
                            </Button>
                        </div>
                    </DialogActions>
                </div>
            </Form>)}
        </Formik>
    </GlobitsPopupV2>);
});

function SelectLevel(props) {
    const {values, setFieldValue} = useFormikContext();
    const {t} = props;
    const [parents, setParents] = useState([]);

    // Hàm gọi API lấy danh sách theo level
    const handleGetAllByLevel = async (level) => {
        if (!level) return;
        try {
            console.log("handleGetAllByLevel", level + 1);
            const {data} = await getAllByLevel(level + 1);
            setParents(data);
        } catch (error) {
            console.error("Error fetching levels:", error);
        }
    };

    // Theo dõi thay đổi của values.level và gọi API ngay lập tức
    useEffect(() => {
        if (values.level) {
            handleGetAllByLevel(values.level);
        }
    }, [values.level]); // Theo dõi trực tiếp values.level để cập nhật ngay lập tức

    // Xử lý thay đổi level
    const handleChange = async (level) => {
        console.log("handleChange", level);

        await setFieldValue("level", level);
        await setFieldValue("parent", null);

        console.log("Updated values.level:", level);
    };

    return (
        <>
            <Grid item sm={12} xs={12}>
                <GlobitsSelectInput
                    label={t("administrativeUnit.level")}
                    name="level"
                    keyValue="value"
                    options={LocalConstants.AdminitractiveLevel}
                    handleChange={(event) => handleChange(event.target.value)}
                />
            </Grid>
            <Grid item sm={12} xs={12}>
                {values.level !== 3 && (
                    <GlobitsAutocomplete
                        name="parent"
                        label={t("administrativeUnit.parent")}
                        options={parents}
                    />
                )}
            </Grid>
        </>
    );
}
