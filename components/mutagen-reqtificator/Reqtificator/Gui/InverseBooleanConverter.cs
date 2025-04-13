﻿using System;
using System.Globalization;
using System.Windows.Data;

namespace Reqtificator.Gui
{
    [ValueConversion(typeof(bool), typeof(bool))]
    internal class InverseBooleanConverter : IValueConverter
    {
        #region IValueConverter Members

        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (targetType != typeof(bool))
            {
                throw new InvalidOperationException("The target must be a boolean");
            }
            bool b = (bool)value;
            return !b;
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (targetType != typeof(bool))
            {
                throw new InvalidOperationException("The target must be a boolean");
            }
            bool b = (bool)value;
            return !b;
        }

        #endregion
    }
}
