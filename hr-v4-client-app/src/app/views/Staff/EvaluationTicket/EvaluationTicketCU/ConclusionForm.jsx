import { Checkbox, FormControlLabel, Grid, Typography } from "@material-ui/core";
import FormGroup from "@material-ui/core/FormGroup";
import GlobitsDateTimePicker from "../../../../common/form/GlobitsDateTimePicker";
import React, { forwardRef } from "react";
import { useFormikContext } from "formik";
import GlobitsPagingAutocomplete from "../../../../common/form/GlobitsPagingAutocomplete";
import { pagingPositionTitle } from "../../../PositionTitle/PositionTitleService";
import GlobitsVNDCurrencyInput from "../../../../common/form/GlobitsVNDCurrencyInput";
import { pagingRankTitle } from "../../../RankTitle/RankTitleService";
import { pagingPosition } from "../../../Position/PositionService";
import {EVALUATE_PERSON} from "../../../../LocalConstants";

const ConclusionForm = ({evaluatePerson}) => {
    const { values, setFieldValue } = useFormikContext();
    return (
        <>
            <Grid item xs={12} md={12}>
                <Typography style={{ fontWeight: "700", fontSize: "large", color: "red" }}>B. KẾT LUẬN:</Typography>
                <Grid item xs={12} md={12}>
                    <FormGroup>
                        {[
                            { id: true, name: "Đạt yêu cầu, đề xuất ký HĐLĐ kể từ ngày" },
                            { id: false, name: "Không đạt yêu cầu" },
                        ].map((contract) => {
                            if (contract.id === true) {
                                return (
                                    <Grid container style={{ display: "flex", gap: 12 }}>
                                        <Grid key={contract.id} className='contract-row' item xs={12} md={12}>
                                            <FormControlLabel
                                                label={
                                                    <Typography style={{ fontWeight: "700" }}>
                                                        {contract.name}
                                                    </Typography>
                                                }
                                                control={
                                                    <Checkbox
                                                        disabled={evaluatePerson === EVALUATE_PERSON.STAFF}
                                                        name='contractRecommendation'
                                                        checked={values?.contractRecommendation === true}
                                                        onChange={(__, value) => {
                                                            if (value) {
                                                                setFieldValue("contractRecommendation", true);
                                                            } else {
                                                                setFieldValue("contractRecommendation", null);
                                                            }
                                                        }}
                                                    />
                                                }
                                            />
                                            {values?.contractRecommendation === true && (
                                                <div className='date-range'>
                                                    <GlobitsDateTimePicker disabled={evaluatePerson === EVALUATE_PERSON.STAFF} name='contractRecommendationDateFrom' />
                                                    <span className='date-label'>Đến ngày</span>
                                                    <GlobitsDateTimePicker disabled={evaluatePerson === EVALUATE_PERSON.STAFF} name='contractRecommendationDateTo' />
                                                </div>
                                            )}
                                        </Grid>
                                        {values?.contractRecommendation === true && (
                                            <Grid container spacing={2}>
                                                <Grid item xs={12} md={3}>
                                                    <GlobitsPagingAutocomplete
                                                        label={"Chức danh"}
                                                        name='positionTitle'
                                                        api={pagingPositionTitle}
                                                        disabled={evaluatePerson === EVALUATE_PERSON.STAFF}
                                                        onChange={(e, selectedPositionTitle) => {
                                                            setFieldValue("positionTitle", selectedPositionTitle);
                                                            setFieldValue("positionTitleId", selectedPositionTitle?.id);
                                                            setFieldValue(
                                                                "positionTitleName",
                                                                selectedPositionTitle?.name
                                                            );
                                                        }}
                                                    />
                                                </Grid>
                                                <Grid item xs={12} md={3}>
                                                    <GlobitsPagingAutocomplete
                                                        label={"Cấp bậc"}
                                                        name='rankTitle'
                                                        api={pagingRankTitle}
                                                        disabled={evaluatePerson === EVALUATE_PERSON.STAFF}
                                                        onChange={(e, selectedRankTitle) => {
                                                            setFieldValue("rankTitle", selectedRankTitle);
                                                            setFieldValue("rankTitleId", selectedRankTitle?.id);
                                                            setFieldValue("rankTitleName", selectedRankTitle?.name);
                                                        }}
                                                    />
                                                </Grid>
                                                <Grid item xs={12} md={3}>
                                                    <GlobitsVNDCurrencyInput
                                                        label={"Lương cứng"}
                                                        disabled={evaluatePerson === EVALUATE_PERSON.STAFF}
                                                        name='baseSalary'
                                                        suffix={"VND"}
                                                        value={values?.baseSalary}
                                                    />
                                                </Grid>
                                                <Grid item xs={12} md={3}>
                                                    <GlobitsVNDCurrencyInput
                                                        label={"Phụ cấp"}
                                                        disabled={evaluatePerson === EVALUATE_PERSON.STAFF}
                                                        name='allowanceAmount'
                                                        suffix={"VND"}
                                                        value={values?.allowanceAmount}
                                                    />
                                                </Grid>
                                                <Grid item xs={12} md={3}>
                                                    <GlobitsDateTimePicker
                                                        disabled={evaluatePerson === EVALUATE_PERSON.STAFF}
                                                        label={"Thời gian áp dụng: Từ ngày"}
                                                        name='effectiveFromDate'
                                                    />
                                                </Grid>
                                            </Grid>
                                        )}
                                    </Grid>
                                );
                            } else {
                                return (
                                    <>
                                        <FormControlLabel
                                            key={contract.id}
                                            control={
                                                <Checkbox
                                                    disabled={evaluatePerson === EVALUATE_PERSON.STAFF}
                                                    name='contractRecommendation'
                                                    checked={values?.contractRecommendation === contract.id}
                                                    onChange={(__, value) => {
                                                        if (value) {
                                                            setFieldValue("contractRecommendation", false);
                                                        } else {
                                                            setFieldValue("contractRecommendation", null);
                                                        }
                                                    }}
                                                />
                                            }
                                            label={contract.name}
                                        />
                                        {values?.contractRecommendation === contract.id && (
                                            <>
                                                {[
                                                    { id: true, name: "Ngừng hợp tác kể từ ngày" },
                                                    { id: false, name: "Bố trí sang vị trí khác" },
                                                ].map((value) => {
                                                    if (value.id === true) {
                                                        return (
                                                            <div style={{ marginLeft: "8px" }}>
                                                                <FormGroup row>
                                                                    <FormControlLabel
                                                                        key={value.id}
                                                                        control={
                                                                            <Checkbox
                                                                                disabled={evaluatePerson === EVALUATE_PERSON.STAFF}
                                                                                name='cooperationStatus'
                                                                                checked={
                                                                                    values?.cooperationStatus ===
                                                                                    value.id
                                                                                }
                                                                                onChange={(__, value) => {
                                                                                    if (value) {
                                                                                        setFieldValue(
                                                                                            "cooperationStatus",
                                                                                            true
                                                                                        );
                                                                                    } else {
                                                                                        setFieldValue(
                                                                                            "cooperationStatus",
                                                                                            null
                                                                                        );
                                                                                    }
                                                                                }}
                                                                            />
                                                                        }
                                                                        label={value.name}
                                                                    />
                                                                    {values?.cooperationStatus === true && (
                                                                        <div className='date-range'>
                                                                            <GlobitsDateTimePicker name='collaborationEndDate' disabled={evaluatePerson === EVALUATE_PERSON.STAFF} />
                                                                        </div>
                                                                    )}
                                                                </FormGroup>
                                                            </div>
                                                        );
                                                    } else {
                                                        return (
                                                            <div style={{ marginLeft: "8px" }}>
                                                                <FormControlLabel
                                                                    key={value.id}
                                                                    control={
                                                                        <Checkbox
                                                                            disabled={evaluatePerson === EVALUATE_PERSON.STAFF}
                                                                            name='cooperationStatus'
                                                                            checked={
                                                                                values?.cooperationStatus === value.id
                                                                            }
                                                                            onChange={(__, value) => {
                                                                                if (value) {
                                                                                    setFieldValue(
                                                                                        "cooperationStatus",
                                                                                        false
                                                                                    );
                                                                                } else {
                                                                                    setFieldValue(
                                                                                        "cooperationStatus",
                                                                                        null
                                                                                    );
                                                                                }
                                                                            }}
                                                                        />
                                                                    }
                                                                    label={value.name}
                                                                />
                                                                {values?.cooperationStatus === false && (
                                                                    <Grid container spacing={2}>
                                                                        <Grid item xs={12} md={3}>
                                                                            <GlobitsPagingAutocomplete
                                                                                disabled={evaluatePerson === EVALUATE_PERSON.STAFF}
                                                                                label={"Vị trí khác"}
                                                                                name='newPosition'
                                                                                api={pagingPosition}
                                                                                onChange={(e, selectedNewPosition) => {
                                                                                    setFieldValue(
                                                                                        "newPosition",
                                                                                        selectedNewPosition
                                                                                    );
                                                                                    setFieldValue(
                                                                                        "newPositionId",
                                                                                        selectedNewPosition?.id
                                                                                    );
                                                                                    setFieldValue(
                                                                                        "newPositionName",
                                                                                        selectedNewPosition?.name
                                                                                    );
                                                                                }}
                                                                                ListboxComponent={forwardRef(
                                                                                    (props, ref) => {
                                                                                        const { children, ...other } =
                                                                                            props;
                                                                                        return (
                                                                                            <ul {...other} ref={ref}>
                                                                                                <ol
                                                                                                    className={
                                                                                                        "custom-listbox"
                                                                                                    }>
                                                                                                    <li>
                                                                                                        <li
                                                                                                            className={
                                                                                                                "header"
                                                                                                            }>
                                                                                                            <p>Mã</p>
                                                                                                            <p>
                                                                                                                Tên vị
                                                                                                                trí
                                                                                                            </p>
                                                                                                            <p>
                                                                                                                Phòng
                                                                                                                ban
                                                                                                            </p>
                                                                                                            <p>
                                                                                                                Nhân
                                                                                                                viên
                                                                                                            </p>
                                                                                                        </li>
                                                                                                        {children}
                                                                                                    </li>
                                                                                                </ol>
                                                                                            </ul>
                                                                                        );
                                                                                    }
                                                                                )}
                                                                                renderOption={(newPosition) => (
                                                                                    <>
                                                                                        <p
                                                                                            data-tooltip={
                                                                                                newPosition?.code
                                                                                            }>
                                                                                            <b>{newPosition?.code}</b>
                                                                                        </p>
                                                                                        <p
                                                                                            data-tooltip={
                                                                                                newPosition?.name
                                                                                            }>
                                                                                            <b>{newPosition?.name}</b>
                                                                                        </p>
                                                                                        <p
                                                                                            data-tooltip={
                                                                                                newPosition?.department
                                                                                                    ?.name
                                                                                            }>
                                                                                            <b>
                                                                                                {
                                                                                                    newPosition
                                                                                                        ?.department
                                                                                                        ?.name
                                                                                                }
                                                                                            </b>
                                                                                        </p>
                                                                                        <p
                                                                                            data-tooltip={
                                                                                                newPosition?.staff
                                                                                                    ?.displayName
                                                                                            }>
                                                                                            <b>
                                                                                                {
                                                                                                    newPosition?.staff
                                                                                                        ?.displayName
                                                                                                }
                                                                                            </b>
                                                                                        </p>
                                                                                    </>
                                                                                )}
                                                                            />
                                                                        </Grid>
                                                                        <Grid item xs={12} md={3}>
                                                                            <GlobitsDateTimePicker
                                                                                disabled={evaluatePerson === EVALUATE_PERSON.STAFF}
                                                                                label={"Từ ngày"}
                                                                                name='newPositionTransferDate'
                                                                            />
                                                                        </Grid>
                                                                    </Grid>
                                                                )}
                                                            </div>
                                                        );
                                                    }
                                                })}
                                            </>
                                        )}
                                    </>
                                );
                            }
                        })}
                    </FormGroup>
                </Grid>
            </Grid>
        </>
    );
};
export default ConclusionForm;
