import { DialogContent, Grid } from "@material-ui/core";
import GlobitsPopup from "app/common/GlobitsPopup";
import { pagingPosition } from "app/views/Position/PositionService";
import React from "react";
import { toast } from "react-toastify";
import { useFormikContext } from "formik";
import { pagingPositionTitle } from "../../../PositionTitle/PositionTitleService";

export default function ViewListPositionTitlePopup ({open, handleClose, node}) {
  const {values} = useFormikContext ();
  const matchedNode = values?.orgChartData?.nodes.find (n => n?.objectId === node?.data?.objectId);
  const departmentId = matchedNode?.objectId;
  const departmentCode = matchedNode?.code;

  const [listPositionTitles, setListPositionTitles] = React.useState ([]);
  React.useEffect (() => {
    if (departmentId || departmentCode) {
      const payload = {
        departmentId,
        isExportExcel:true,
        departmentCode
      }
      pagingPositionTitle (payload)
          .then (({data}) => {
            setListPositionTitles (data?.content || []);
          })
          .catch ((err) => {
            console.error (err);
            toast.error ("Có lỗi xảy ra vui lòng thử lại");
          });
    }
  }, [departmentId, departmentCode]);

  return (
      <GlobitsPopup
          popupId='edit-node-popup'
          scroll={"body"}
          size='xs'
          open={open}
          noDialogContent
          title={`Danh sách Chức danh`}
          onClosePopup={handleClose}>
        <div className='dialog-body'>
          <DialogContent className='p-12'>
            <Grid container spacing={2}>
              {listPositionTitles?.length > 0? (
                  listPositionTitles.map ((positionTitle, index) => {
                    let className = "flex flex-middle";
                    if (index % 2) {
                      className += " bg-slate-200";
                    }
                    return (
                        <Grid item xs={12} className={className}>
                          <div>
                            <h5 className='text-dark-green mb-0'>{positionTitle?.name}</h5>
                          </div>
                        </Grid>
                    );
                  })
              ) : (
                  <Grid item xs={12}>
                    Không có dữ liệu
                  </Grid>
              )}
            </Grid>
          </DialogContent>
        </div>
      </GlobitsPopup>
  );
}
