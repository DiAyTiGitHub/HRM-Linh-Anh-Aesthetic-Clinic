import { observer } from "mobx-react";
import React, { memo, useState } from "react";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import { useHistory } from "react-router-dom";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import DeleteIcon from "@material-ui/icons/Delete";
import AddIcon from "@material-ui/icons/Add";
import SearchIcon from '@material-ui/icons/Search';
import NoteIcon from "@material-ui/icons/Note";
import ConstantList from "app/appConfig";
import { Form, Formik } from "formik";
import { Grid, Button, IconButton, Tabs, Tab, AppBar, makeStyles, ButtonGroup } from "@material-ui/core";
import { useStore } from "../../../stores";
import { useTranslation } from "react-i18next";
import DoneAllIcon from '@material-ui/icons/DoneAll';
import ThumbDownIcon from '@material-ui/icons/ThumbDown';
import FilterListIcon from '@material-ui/icons/FilterList';
import CandidateFilter from "./CandidatesInRecruitmentFilter";
import CandidatesInRecruitmentFilter from "./CandidatesInRecruitmentFilter";

function CandidatesInRecruitmentToolbar() {
    const history = useHistory();
    const { candidateStore } = useStore();
    const { t } = useTranslation();

    const {
        handleDeleteList,
        pagingCandidates,
        searchObject,
        listOnDelete,
        handleSetSearchObject,
        handleOpenRejectPopup,
        handleOpenApprovePopup
    } = candidateStore;

    async function handleFilter(values) {
        const newSearchObject = {
            ...values,
            pageIndex: 1,
        };

        handleSetSearchObject(newSearchObject);
        await pagingCandidates();
    }

    function handleApproveCandidate() {
        handleOpenApprovePopup();
    }

    function handleDeclineCandidate() {
        handleOpenRejectPopup();
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
                            <Grid container spacing={2}>
                                <Grid item xs={12} md={6}>
                                    <ButtonGroup
                                        color="container"
                                        aria-label="outlined primary button group"
                                    >
                                        <Button
                                            startIcon={<AddIcon />}
                                            type="button"
                                            onClick={() => {
                                                history.push(ConstantList.ROOT_PATH + `candidate/new-candidate?isFromCandidatesInRecruitment=true`)
                                            }}
                                        >
                                            Thêm mới
                                        </Button>

                                        <Button
                                            disabled={listOnDelete?.length <= 0}
                                            startIcon={<DoneAllIcon />}
                                            onClick={handleApproveCandidate}
                                        >
                                            Duyệt
                                        </Button>

                                        <Button
                                            disabled={listOnDelete?.length <= 0}
                                            startIcon={<ThumbDownIcon />}
                                            onClick={handleDeclineCandidate}
                                        >
                                            Từ chối
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
                                        // InputProps={{
                                        //     endAdornment: (
                                        //         <IconButton className="py-0 px-4" aria-label="search" type="submit">
                                        //             <SearchIcon />
                                        //         </IconButton>
                                        //     ),
                                        // }}
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
                                            <Button
                                                startIcon={<FilterListIcon className={` filterRotateIcon ${isOpenFilter && 'onRotate'}`} />}
                                                className=" d-inline-flex py-2 px-8 btnHrStyle"
                                                onClick={handleTogglePopupFilter}
                                            >
                                                Bộ lọc
                                            </Button>
                                        </ButtonGroup>
                                    </div>
                                </Grid>
                            </Grid>

                            <CandidatesInRecruitmentFilter
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

export default memo(observer(CandidatesInRecruitmentToolbar));