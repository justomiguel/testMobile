﻿#ExternalChecksum("C:\Users\Carlos\Documents\Visual Studio 2012\Projects\SysAcad\SysAcad\PivotPage1.xaml","{406ea660-64cf-4c82-b6f0-42d48172a799}","3FB5C5E99769F1E2E037D67DABE409E2")
'------------------------------------------------------------------------------
' <auto-generated>
'     Este código fue generado por una herramienta.
'     Versión de runtime:4.0.30319.34011
'
'     Los cambios en este archivo podrían causar un comportamiento incorrecto y se perderán si
'     se vuelve a generar el código.
' </auto-generated>
'------------------------------------------------------------------------------

Option Strict Off
Option Explicit On

Imports Microsoft.Phone.Controls
Imports System
Imports System.Windows
Imports System.Windows.Automation
Imports System.Windows.Automation.Peers
Imports System.Windows.Automation.Provider
Imports System.Windows.Controls
Imports System.Windows.Controls.Primitives
Imports System.Windows.Data
Imports System.Windows.Documents
Imports System.Windows.Ink
Imports System.Windows.Input
Imports System.Windows.Interop
Imports System.Windows.Markup
Imports System.Windows.Media
Imports System.Windows.Media.Animation
Imports System.Windows.Media.Imaging
Imports System.Windows.Resources
Imports System.Windows.Shapes
Imports System.Windows.Threading



<Microsoft.VisualBasic.CompilerServices.DesignerGenerated()>  _
Partial Public Class PivotPage1
    Inherits Microsoft.Phone.Controls.PhoneApplicationPage
    
    Friend WithEvents LayoutRoot As System.Windows.Controls.Grid
    
    Friend WithEvents Pivot As Microsoft.Phone.Controls.Pivot
    
    Friend WithEvents btCalendarioAcad As System.Windows.Controls.Button
    
    Friend WithEvents btHorariosCursado As System.Windows.Controls.Button
    
    Friend WithEvents PivotSysacad As Microsoft.Phone.Controls.PivotItem
    
    Friend WithEvents tbOffline As System.Windows.Controls.TextBlock
    
    Friend WithEvents btEstadoAcademico As System.Windows.Controls.Button
    
    Friend WithEvents btExamenes As System.Windows.Controls.Button
    
    Friend WithEvents btCursado As System.Windows.Controls.Button
    
    Friend WithEvents btCorrCursado As System.Windows.Controls.Button
    
    Friend WithEvents btCorrRendir As System.Windows.Controls.Button
    
    Friend WithEvents btInscExamen As System.Windows.Controls.Button
    
    Friend WithEvents btInscCursado As System.Windows.Controls.Button
    
    Private _contentLoaded As Boolean
    
    '''<summary>
    '''InitializeComponent
    '''</summary>
    <System.Diagnostics.DebuggerNonUserCodeAttribute()>  _
    Public Sub InitializeComponent()
        If _contentLoaded Then
            Return
        End If
        _contentLoaded = true
        System.Windows.Application.LoadComponent(Me, New System.Uri("/SysAcad;component/PivotPage1.xaml", System.UriKind.Relative))
        Me.LayoutRoot = CType(Me.FindName("LayoutRoot"),System.Windows.Controls.Grid)
        Me.Pivot = CType(Me.FindName("Pivot"),Microsoft.Phone.Controls.Pivot)
        Me.btCalendarioAcad = CType(Me.FindName("btCalendarioAcad"),System.Windows.Controls.Button)
        Me.btHorariosCursado = CType(Me.FindName("btHorariosCursado"),System.Windows.Controls.Button)
        Me.PivotSysacad = CType(Me.FindName("PivotSysacad"),Microsoft.Phone.Controls.PivotItem)
        Me.tbOffline = CType(Me.FindName("tbOffline"),System.Windows.Controls.TextBlock)
        Me.btEstadoAcademico = CType(Me.FindName("btEstadoAcademico"),System.Windows.Controls.Button)
        Me.btExamenes = CType(Me.FindName("btExamenes"),System.Windows.Controls.Button)
        Me.btCursado = CType(Me.FindName("btCursado"),System.Windows.Controls.Button)
        Me.btCorrCursado = CType(Me.FindName("btCorrCursado"),System.Windows.Controls.Button)
        Me.btCorrRendir = CType(Me.FindName("btCorrRendir"),System.Windows.Controls.Button)
        Me.btInscExamen = CType(Me.FindName("btInscExamen"),System.Windows.Controls.Button)
        Me.btInscCursado = CType(Me.FindName("btInscCursado"),System.Windows.Controls.Button)
    End Sub
End Class

