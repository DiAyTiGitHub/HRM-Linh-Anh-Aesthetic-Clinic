import { observer } from "mobx-react";
import React, { memo, useState } from "react";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import { useHistory } from "react-router-dom";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import AddIcon from "@material-ui/icons/Add";
import SearchIcon from '@material-ui/icons/Search';
import ConstantList from "app/appConfig";
import { Form, Formik } from "formik";
import { Grid, Button, IconButton, Tabs, Tab, AppBar, makeStyles, ButtonGroup } from "@material-ui/core";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import CandidateRecruitmentRoundFilter from "./CandidateRecruitmentRoundFilter";
import HourglassFullIcon from '@material-ui/icons/HourglassFull';
import QueuePlayNextIcon from '@material-ui/icons/QueuePlayNext';

function CandidateRecruitmentRoundToolbar() {
    const history = useHistory();
    const { t } = useTranslation();

    const {
        candidateRecruitmentRoundStore,
        recruitmentStore
    } = useStore();

    const {
        handleDeleteList,
        pagingCandidateRecruitmentRound,
        searchObject,
        listOnDelete,
        handleSetSearchObject,
        handleOpenCreateEdit,

        handleOpenMoveToNextRoundPopup,
        handleOpenUpdateResultPopup,

        isLastRecruitmentRound
    } = candidateRecruitmentRoundStore;

    const {
        selectedRecruitment
    } = recruitmentStore;

    async function handleFilter(values) {
        const newSearchObject = {
            ...values,
            pageIndex: 1,
        };
        handleSetSearchObject(newSearchObject);
        await pagingCandidateRecruitmentRound();
    }

    const [isOpenFilter, setIsOpenFilter] = useState(false);

    function handleCloseFilter() {
        if (isOpenFilter) {
            setIsOpenFilter(false);
        }
    }

    function handleOpenFilter() {
        if (!isOpenFilter) {
            setIsOpenFilter(true);
        }
    }
    function handleTogglePopupFilter() {
        if (isOpenFilter) handleCloseFilter();
        else handleOpenFilter();
    }

    return (
        <Formik
            enableReinitialize
            initialValues={JSON.parse(JSON.stringify(searchObject))}
            onSubmit={handleFilter}
        >
            {({ resetForm, values, setFieldValue, setValues }) => {
                return (
                    <Form autoComplete="off">
                        <Grid item xs={12}>
                            <Grid container spacing={2} className="justify-end">
                                <Grid item xs={12} >
                                    <ButtonGroup
                                        color="container"
                                        aria-label="outlined primary button group"
                                    >
                                        <Button
                                            startIcon={<AddIcon />}
                                            type="button"
                                            onClick={() => handleOpenCreateEdit()}
                                        >
                                            Thêm vào DS dự tuyển
                                        </Button>

                                        <Button
                                            startIcon={<HourglassFullIcon />}
                                            type="button"
                                            disabled={listOnDelete?.length <= 0}
                                            onClick={handleOpenUpdateResultPopup}
                                        >
                                            Cập nhật kết quả
                                        </Button>

                                        <Button
                                            startIcon={<QueuePlayNextIcon />}
                                            type="button"
                                            disabled={listOnDelete?.length <= 0}
                                            onClick={handleOpenMoveToNextRoundPopup}
                                        >
                                            {isLastRecruitmentRound(selectedRecruitment) ? "Đánh dấu đã ĐẠT kì thi tuyển" : "Chuyển đến vòng thi tiếp"}
                                        </Button>

                                        <Button
                                            disabled={listOnDelete?.length <= 0}
                                            startIcon={<DeleteOutlineIcon />}
                                            onClick={handleDeleteList}
                                        >
                                            {t("general.button.delete")}
                                        </Button>
                                    </ButtonGroup>
                                </Grid>

                                <Grid item xs={12} md={6}>
                                    <div className="flex justify-between align-center">
                                        <GlobitsTextField
                                            placeholder="Tìm kiếm theo mã, tên ứng viên..."
                                            name="keyword"
                                            variant="outlined"
                                            notDelay
                                        />

                                        <ButtonGroup
                                            className="filterButtonV4"
                                            color="container"
                                            aria-label="outlined primary button group"
                                        >
                                            <Button
                                                startIcon={<SearchIcon className={``} />}
                                                className="ml-8 d-inline-flex py-2 px-8 btnHrStyle"
                                                type="submit"
                                            >
                                                Tìm kiếm
                                            </Button>
                                            {/* <Button
                                                startIcon={<FilterListIcon className={` filterRotateIcon ${isOpenFilter && 'onRotate'}`} />}
                                                className=" d-inline-flex py-2 px-8 btnHrStyle"
                                                onClick={handleTogglePopupFilter}
                                            >
                                                Bộ lọc
                                            </Button> */}
                                        </ButtonGroup>
                                    </div>
                                </Grid>
                            </Grid>

                            <CandidateRecruitmentRoundFilter
                                handleFilter={handleFilter}
                                handleCloseFilter={handleCloseFilter}
                                isOpenFilter={isOpenFilter}
                            />
                        </Grid>
                    </Form>
                );
            }}
        </Formik>
    );
}

export default memo(observer(CandidateRecruitmentRoundToolbar));