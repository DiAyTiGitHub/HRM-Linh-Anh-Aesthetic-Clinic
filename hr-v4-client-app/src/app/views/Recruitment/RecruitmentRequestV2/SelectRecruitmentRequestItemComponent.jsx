import {Button, ButtonGroup, Icon, IconButton} from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import {RecruitmentRequestItem} from "app/common/Model/Assets";
import {pagingPositionTitle} from "app/views/PositionTitle/PositionTitleService";
import {FieldArray, useFormikContext} from "formik";
import {observer} from "mobx-react";
import React, {memo} from "react";

const SelectRecruitmentRequestItemComponent = () => {
    const {values, setFieldValue} = useFormikContext();

    return (<FieldArray name='recruitmentRequestItems'>
        {({push, remove}) => (<>
            <div className='flex items-center justify-between my-8'>
                <ButtonGroup
                    color="container"
                    aria-label="outlined primary button group"
                >
                    <Button
                        startIcon={<AddIcon/>}
                        onClick={() => push({...new RecruitmentRequestItem()})}
                    >
                        Thêm chức danh cần tuyển
                    </Button>
                </ButtonGroup>
            </div>

            <div className='table-root table-form'>
                <table className='table-container' cellPadding={0} cellSpacing={0}>
                    <thead>
                    <tr>
                        <th width='10%'>Thao tác</th>
                        <th align='left'>Chức danh cần tuyển</th>
                        <th align='left'>Số trong kế hoạch</th>
                        <th align='left'>Số bổ sung</th>
                        <th align='left'>Tổng Số</th>
                        <th align='left'>Số đã công bố</th>
                    </tr>
                    </thead>
                    <tbody>
                    {values?.recruitmentRequestItems?.length > 0 ? (
                            values?.recruitmentRequestItems?.map((item, index) =>
                                (
                                    <tr key={index} className='row-table-body'>
                                        <td align='center' width='10%'>
                                            <IconButton size='small' onClick={() => remove(index)}>
                                                <Icon fontSize='small' color='secondary'>
                                                    delete
                                                </Icon>
                                            </IconButton>
                                        </td>
                                        <td>
                                            <GlobitsPagingAutocompleteV2
                                                name={`recruitmentRequestItems[${index}].positionTitle`}
                                                api={pagingPositionTitle}
                                            />
                                        </td>
                                        <td>
                                            <GlobitsTextField
                                                type='number'
                                                name={`recruitmentRequestItems[${index}].inPlanQuantity`}
                                                onChange={(e) => {
                                                    const updatedValue = parseInt(e.target.value) || 0;
                                                    setFieldValue(`recruitmentRequestItems[${index}].inPlanQuantity`, updatedValue);
                                                    setFieldValue(`recruitmentRequestItems[${index}].totalQuantity`, updatedValue + (values.recruitmentRequestItems[index]?.extraQuantity || 0));
                                                }}
                                            />
                                        </td>
                                        <td>
                                            <GlobitsTextField
                                                type='number'
                                                name={`recruitmentRequestItems[${index}].extraQuantity`}
                                                onChange={(e) => {
                                                    const updatedValue = parseInt(e.target.value) || 0;
                                                    setFieldValue(`recruitmentRequestItems[${index}].extraQuantity`, updatedValue);
                                                    setFieldValue(`recruitmentRequestItems[${index}].totalQuantity`, (values.recruitmentRequestItems[index]?.inPlanQuantity || 0) + updatedValue);
                                                }}
                                            />
                                        </td>
                                        <td>
                                            <GlobitsTextField
                                                type='number'
                                                name={`recruitmentRequestItems[${index}].totalQuantity`}
                                                disabled
                                            />
                                        </td>
                                        <td>
                                            <GlobitsTextField
                                                type='number'
                                                name={`recruitmentRequestItems[${index}].announcementQuantity`}
                                            />
                                        </td>
                                    </tr>))
                        ) :
                        (
                            <tr className='row-table-body row-table-no_data'>
                                <td colSpan={6} align='center' className="py-8">Chưa có phần tử nào
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
        </>)}
    </FieldArray>);
};

export default memo(observer(SelectRecruitmentRequestItemComponent));
