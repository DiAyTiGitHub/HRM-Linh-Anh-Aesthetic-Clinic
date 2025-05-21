import React, { useEffect, memo } from "react";
import { Formik, Form } from "formik";
import { Button, IconButton } from "@material-ui/core";
import SearchIcon from '@material-ui/icons/Search';
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsTextField from "app/common/form/GlobitsTextField";

function SearchSubDepartmentsInPopup(props) {
    const { t } = useTranslation();
    const { departmentStore } = useStore();
    const {
        pagingSubDeparments,
        handleChangePageSubDepartments,
        handleSetSearchObjectSubDepartments,
        totalElementsSubDepartments,
        totalPagesSubDepartments,
        resetStoreSubDepartments,
        searchObjectSubDepartments,
        openChooseSubDpmPopup: openPopup
    } = departmentStore;

    

    async function handleSearch(values) {
        handleSetSearchObjectSubDepartments(values);
        await pagingSubDeparments();
    }

    return (
        <Formik
            enableReinitialize
            initialValues={{ ...searchObjectSubDepartments }}
            onSubmit={handleSearch}
        >
            {({ isSubmitting, values, setFieldValue, initialValues }) => (
                <Form autoComplete="off">
                    <FormikFocusError />
                    <div className="flex justify-between align-center">
                        <GlobitsTextField
                            placeholder="Tìm kiếm đơn vị khác..."
                            name="keyword"
                            variant="outlined"
                            notDelay
                            timeOut={0}
                            InputProps={{
                                endAdornment: (
                                    <IconButton className="py-0 px-4" aria-label="search" type="submit">
                                        <SearchIcon />
                                    </IconButton>
                                ),
                            }}
                        />

                        <Button
                            startIcon={<SearchIcon className={`mr-2`} />}
                            className="ml-8 d-inline-flex filterButtonV4 bgc-warning-d1 py-2 px-8 btn text-white"
                            type="submit"
                        >
                            Tìm kiếm
                        </Button>
                    </div>
                </Form>
            )}
        </Formik>
    );
}

export default memo(observer(SearchSubDepartmentsInPopup));
