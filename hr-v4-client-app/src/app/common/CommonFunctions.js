import LocalConstants from "app/LocalConstants";
import localStorageService from "app/services/localStorageService";
import moment from "moment";
import i18n from "i18n";
import React from "react";

export const t = (...args) => {
  return i18n.t(...args);
}

export const transformDateDatePicker = (_, originalValue) => {
  return (originalValue && moment(originalValue).isValid()) ? new Date(originalValue) : (originalValue ? originalValue : null);
};

export function getAgeFromDates(startDate, endDate) {
  let age;
  if (
    startDate &&
    moment(startDate).isValid() &&
    endDate &&
    moment(endDate).isValid()
  ) {
    const date1 = new Date(endDate);
    const date2 = new Date(startDate);
    age = date1.getFullYear() - date2.getFullYear();
    const m = date1.getMonth() - date2.getMonth();
    if (m < 0 || (m === 0 && date1.getDate() < date2.getDate())) {
      age--;
    }
  }
  return age;
}

//dùng cho các trường date trong formik (transform, typeError, maxDate, minDate)
//update 7/10/22: thêm tham số fieldName(không bắt buộcc) để truyền cảnh báo tên trường nào không đúng định dạng
export function dateYupTypeValidation(schema, field, fieldName) {
  const typeErrorText = fieldName
    ? `${fieldName} ${t("validation.errorDate")}`
    : t("validation.typeError");
  return schema
    .transform(transformDateDatePicker)
    .typeError(typeErrorText)
    .test(`${field}-min-year`, typeErrorText, function (value) {
      if (value && moment(value).isValid()) {
        if (new Date(value).getFullYear() < 1900) {
          return false;
        }
      }
      return true;
    })
    .test(`${field}-max-year`, typeErrorText, function (value) {
      if (value && moment(value).isValid()) {
        if (new Date(value).getFullYear() > 2100) {
          return false;
        }
      }
      return true;
    });
}

//hàm để lấy các trường mặc định nếu đăng nhập tài khoản đơn vị
export const getDefaultOrg = () => {
  let region = null
  let province = null
  let org = null
  let department = null
  let district = null;
  let ward = null;
  if (localStorageService.isOrg() || localStorageService.isDepartment()) {
    // district = localStorageService.getCurrentOrg()?.districtUnit || null
    province = localStorageService.getCurrentOrg()?.provinceUnit || null
    region = province?.parent || null
    org = localStorageService.getCurrentOrg() || null
    if (localStorageService.isDepartment()) {
      department = localStorageService.getCurrentOrg()?.department || null
    }
  } else if (localStorageService.isRegion()) {
    region = localStorageService.getAdminUnit() || null
  } else if (localStorageService.isProvince()) {
    province = localStorageService.getAdminUnit() || null
    region = province?.parent || null
  } else if (localStorageService.isDistrict()) {
    district = localStorageService.getAdminUnit() || null
    province = district?.parent || null;
    region = province?.parent || null;
  } else if (localStorageService.isWard()) {
    ward = localStorageService.getAdminUnit() || null
    district = ward?.parent || null
    province = district?.parent || null;
    region = province?.parent || null;
  }
  return { region, province, org, department, district, ward }
};

//hàm để check xn M2 có nhập kết quả chưa
export const isM2HasResult = (diagnosticReport) => {
  if (diagnosticReport?.mtb) {
    return true;
  }
  if (diagnosticReport?.itemObss?.length > 0) {
    return diagnosticReport.itemObss.some((itemObs) => {
      switch (itemObs?.item?.datatype) {
        case LocalConstants.DATATYPE_NUMERIC: {
          if (!Number.isNaN(Number.parseFloat(itemObs?.valueNumeric))) {
            return true;
          }
          break;
        }
        case LocalConstants.DATATYPE_DATE: {
          if (itemObs?.valueDate) {
            return true;
          }
          break;
        }
        case LocalConstants.DATATYPE_BOOLEAN: {
          if (this.isBoolean(itemObs?.valueBoolean)) {
            return true;
          }
          break;
        }
        case LocalConstants.DATATYPE_CODED: {
          if (itemObs?.valueCoded) {
            if (
              itemObs.valueCoded?.name === "Chờ kết quả" ||
              itemObs.valueCoded?.code === "H.1"
            ) {
              return false;
            }
            return true;
          }
          break;
        }
        default: {
          return false;
        }
      }
      return false;
    });
  }
  return false;
};

//dùng cho các trường textbox trong formik để kiểm tra ký tự đặc biệt
export function textYupFormatValidation(schema, field, fieldName) {
  const typeErrorText = fieldName
    ? `${fieldName} - ${t("validation.specialChar")}`
    : t("validation.specialChar");
  return (
    schema
      .typeError(typeErrorText)
      // .matches(LocalConstants.REGEX_SPECIAL_CHARACTERS, fieldName ? `${fieldName} chứa ký tự không hợp lệ` : "Chứa ký tự không hợp lệ")
      .test("special-char", typeErrorText, (value) => {
        let newValue = value
          ?.normalize("NFD")
          ?.replace(/[\u0300-\u036f]/g, "")
          ?.replace(/đ/g, "d")
          ?.replace(/Đ/g, "D");
        if (
          value &&
          newValue &&
          newValue.match(/^[.,;?/:'"@&%()/|=!#-\w\s\d\n\*\+]*$/) === null
        ) {
          return false;
        }
        return true;
      })
  );
}

//find the first selected (pathName and value) M2 test
export const findSelectedLabTest = (encounter) => {
  if (encounter?.orderTest?.requestTypes?.length > 0) {
    for (let i = 0; i < encounter.orderTest.requestTypes.length; i++) {
      const request = encounter.orderTest.requestTypes[i];
      if (request?.listLabTests?.length > 0) {
        for (let j = 0; j < request.listLabTests.length; j++) {
          const labTest = request.listLabTests[j];
          if (labTest?.selected) {
            return [`orderTest.requestTypes[${i}].listLabTests[${j}]`, labTest];
          }
        }
      }
    }
    // for (const request of encounter.orderTest.requestTypes) {
    //     return request?.listLabTests?.find(labTest => {
    //         return labTest?.selected
    //     })
    // }
  }
  return [];
};

export function getTextWidth(text, fontSize) {
  let element = document.createElement("span");
  document.body.appendChild(element);

  element.style.height = "auto";
  element.style.width = "auto";
  element.style.position = "absolute";
  element.style.zIndex = "-999";
  element.style.whiteSpace = "no-wrap";
  if (fontSize) {
    element.style.fontSize = fontSize + "px";
  }
  element.innerHTML = text + "";

  const width = Math.ceil(element.clientWidth);
  document.body.removeChild(element);
  return width;
}

//lấy quý theo tháng trong năm (month: 1->12)
export const getQuarterFromMonth = (month) => {
  return Math.ceil(month / 3);
};

//lấy dữ liệu mặc định cho bảng báo cáo (mặc định index)
export const getReportDefaultDataFromRows = (rows = [], cols) => {
  return rows.reduce((acc, row) => {
    if (!row.index) {
      return acc;
    }
    let item = { index: row.index }
    if (cols?.length > 0) {
      cols.forEach(col => {
        if (col.value) {
          if (row.type === "totalViewOnly" && col.value.includes("rate")) {
            item[col.value] = 100;
          } else {
            item[col.value] = 0;
          }
        }
      })
    }
    return [...acc, item];
  }, []);
};

//component
export const RequiredLabel = React.memo(() => {
  return (
    <span className="text-red"> * </span>
  )
})

export function containsOnlyNumbers(str) {
  return /^\d+$/.test(str);
}

/**
 * @returns The browser name.
 */
export const browserName = (function () {
  var test = function (regexp) { return regexp.test(window.navigator.userAgent) }
  switch (true) {
    case test(/edg/i): return "Microsoft Edge";
    case test(/trident/i): return "Microsoft Internet Explorer";
    case test(/firefox|fxios/i): return "Mozilla Firefox";
    case test(/opr\//i): return "Opera";
    case test(/ucbrowser/i): return "UC Browser";
    case test(/samsungbrowser/i): return "Samsung Browser";
    case test(/chrome|chromium|crios/i): return "Google Chrome";
    case test(/safari/i): return "Apple Safari";
    default: return "Other";
  }
})()

export const isFirefox = browserName === "Mozilla Firefox"

// check tab chỉ định xn có trường validate từ errors
export const checkErrorTabClinicalRequest = (encounter, touched) => {
  if (encounter?.orderTest && touched?.orderTest) {
    for (const error of Object.keys(encounter?.orderTest)) {
      if (error !== "hiv" && encounter?.orderTest?.[error] && touched?.orderTest?.[error]) {
        return true
      }
    }
  }
  return false;
}

// check tab chẩn đoán có trường validate từ errors
const DIAGNOSTIC_FIELD = [
  "departmentTcl", "directionTreatment", "solutionTransferDto", "diagnosticDate",
  "typeResistance", "registerType", "otherRegisterType", "patientTransferOut"
]
export const checkErrorTabDiagnostic = (encounter, touched) => {
  for (const field of DIAGNOSTIC_FIELD) {
    if (encounter?.[field] && touched?.[field]) return true
  }
  return false;
}

// check tab phác đồ có trường validate từ errors
export const checkErrorTabMedicationRequest = (encounter, touched) => {
  if (encounter?.patientRegimen && touched?.patientRegimen &&
    encounter?.patientMedicationHistorys && touched?.patientMedicationHistorys) {
    return true;
  }
  return false;
}

// check tab adsm1 & tab adsm2 có trường validate từ errors
export const checkErrorTabAdsm = (encounter, touched) => {
  if (encounter?.adverseEvent && touched?.adverseEvent) {
    return true;
  }
  return false;
}

//check number có phải là số nguyên hay không  nếu không phải chỉ lấy 2 số sau dấu phẩy
export function checkNumberIsInteger(number) {
  if (number !== null && typeof number === 'number') {
    if (Number.isInteger(number)) {
      return number
    } else {
      return Math.round(number * 100) / 100
    }
  } else {
    return 0;
  }
}

export const getFirstOfDay = (date) => {
  if (date && moment(date).isValid()) {
    const ret = new Date(date);
    ret.setHours(0);
    ret.setMinutes(0);
    ret.setSeconds(0);
    return ret;
  }
  return null;
}

export const getLastOfDay = (date) => {
  if (date && moment(date).isValid()) {
    const ret = new Date(date);
    ret.setHours(23);
    ret.setMinutes(59);
    ret.setSeconds(59);
    return ret;
  }
  return null;
}

export function usePrevious(value) {
  const ref = React.useRef();
  React.useEffect(() => {
    ref.current = value; //assign the value of ref to the argument
  }, [value]); //this code will run when the value of 'value' changes
  return ref.current; //in the end, return the current ref value.
}

export function convertToConstantFormat(input) {
  if (typeof input !== 'string' || input.trim() === '') return ''; // Kiểm tra input hợp lệ

  return input
    .normalize("NFD") // Tách các ký tự có dấu
    .replace(/[\u0300-\u036f]/g, "") // Xóa dấu
    .replace(/đ/g, "d") // Thay thế "đ" thành "d"
    .replace(/Đ/g, "D") // Thay thế "Đ" thành "D"
    .replace(/[^a-zA-Z0-9\s]/g, "") // Xóa ký tự đặc biệt
    .toUpperCase() // Chuyển thành chữ hoa
    .trim() // Xóa khoảng trắng thừa đầu/cuối chuỗi
    .replace(/\s+/g, "_"); // Thay khoảng trắng bằng dấu gạch dưới
}
