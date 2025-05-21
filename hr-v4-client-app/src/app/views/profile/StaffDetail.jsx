import { Button, Grid } from "@material-ui/core";
import AssignmentIndIcon from "@material-ui/icons/AssignmentInd";
import VpnKeyIcon from "@material-ui/icons/VpnKey";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { NavLink, useParams } from "react-router-dom/cjs/react-router-dom.min";
import Config from "../../appConfig";
import { useStore } from "../../stores";
import ChangePassword from "./ProfilePopup/ChangePassword";
import GeneralInformation from "./TabContainer/GeneralInformation";
import TabProfileDiagram from "./TabContainer/TabProfileDiagram";

function StaffDetail() {
    const { t } = useTranslation();
    const { staffId } = useParams();
    const { currentStaff, handleOpenChangePassWord } = useStore().profileStore;
    const divChangeInformation = useRef(null);
    const [height, setHeight] = useState(0);

    useEffect(() => {
        if (divChangeInformation.current) {
            setHeight(divChangeInformation.current.clientHeight);
        }
    }, []);

    return (
        <>
            <Formik initialValues={currentStaff} enableReinitialize onSubmit={(values) => {}}>
                <Form autoComplete='off'>
                    <GeneralInformation />
                    <Grid item xs={12} style={{ paddingBottom: height }} />

                    <div
                        ref={divChangeInformation}
                        className='dialog-footer bg-white flex items-center justify-end gap-4'
                        style={{
                            position: "fixed",
                            bottom: "0px",
                            left: "0px",
                            right: "0px",
                            border: "none",
                            boxShadow: "0 5px 5px 5px #333",
                        }}>
                        {!staffId && (
                            <Button
                                className='btn btn-danger d-inline-flex'
                                color='secondary'
                                onClick={handleOpenChangePassWord}
                                startIcon={<VpnKeyIcon className='mr-4' />}>
                                Đổi mật khẩu
                            </Button>
                        )}

                        <Button
                            startIcon={<AssignmentIndIcon className='mr-4' />}
                            component={NavLink}
                            className='btn text-white bgc-limegreen d-inline-flex'
                            to={Config.ROOT_PATH + "staff/edit/" + currentStaff?.id}>
                            {/* {t("general.button.edit")} */}
                            Cập nhật thông tin tài khoản
                        </Button>
                    </div>
                </Form>
            </Formik>

            <ChangePassword />
        </>
    );
}

export default memo(observer(StaffDetail));
