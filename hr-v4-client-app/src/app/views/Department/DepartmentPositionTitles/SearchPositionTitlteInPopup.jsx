import React, { useEffect, memo } from "react";
import { Formik, Form } from "formik";
import { Button, IconButton } from "@material-ui/core";
import SearchIcon from '@material-ui/icons/Search';
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsTextField from "app/common/form/GlobitsTextField";

function SearchPositionTitlteInPopup(props) {
    const { t } = useTranslation();
    const { positionTitleV2Store, departmentStore } = useStore();
    const {
        searchObject,
        pagingPositionTitle,
        resetStore,
        handleSetSearchObject
    } = positionTitleV2Store;

    const { openChoosePSPopup } = departmentStore;

    useEffect(() => {
        if (openChoosePSPopup) {
            pagingPositionTitle();
        }
        return () => {
            resetStore();
        };
    }, [openChoosePSPopup]);

    async function handleSearch(values) {
        handleSetSearchObject(values);
        await pagingPositionTitle();
    }

    return (
        <Formik
            enableReinitialize
            initialValues={{ ...searchObject }}
            onSubmit={handleSearch}
        >
            {({ isSubmitting, values, setFieldValue, initialValues }) => (
                <Form autoComplete="off">
                    <FormikFocusError />
                    <div className="flex justify-between align-center">
                        <GlobitsTextField
                            placeholder="Tìm kiếm theo chức danh..."
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

export default memo(observer(SearchPositionTitlteInPopup));
