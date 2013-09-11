package org.squirrelsql.aliases;

import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class DndDragPositionMarker<T>
{
   public static final Color STROKE_COLOR = Color.BLACK;
   private DndDragPositionMarkableTreeCell<T> _treeCell;

   private Path _upperLinePath = new Path();
   private Path _rightLinePath = new Path();
   private Path _lowerLinePath = new Path();
   public static final int STROKE_WIDTH = 3;

   public DndDragPositionMarker(DndDragPositionMarkableTreeCell<T> treeCell)
   {
      _treeCell = treeCell;

      _treeCell.setOnDragOver(this::onDragOver);

      _treeCell.setOnDragExited(this::onDragExit);

   }

   private void onDragExit(DragEvent dragEvent)
   {
      clearMarks();
   }

   private void clearMarks()
   {
      _treeCell.getChildrenModifiable().remove(_upperLinePath);
      _treeCell.getChildrenModifiable().remove(_lowerLinePath);
      _treeCell.getChildrenModifiable().remove(_rightLinePath);
   }


   private void onDragOver(DragEvent dragEvent)
   {
      if(_treeCell.isEmpty())
      {
         clearMarks();
         return;
      }

      initHorizontalPathLine(true, _upperLinePath);
      initHorizontalPathLine(false, _lowerLinePath);
      initRightPathLine(_rightLinePath);

      if(isOnTheRight(dragEvent))
      {
         _treeCell.getChildrenModifiable().remove(_upperLinePath);
         _treeCell.getChildrenModifiable().remove(_lowerLinePath);
         if (false == _treeCell.getChildrenModifiable().contains(_rightLinePath))
         {
            _treeCell.getChildrenModifiable().add(_rightLinePath);
         }
      }
      else if(isOnTheUpper(dragEvent))
      {
         _treeCell.getChildrenModifiable().remove(_rightLinePath);
         _treeCell.getChildrenModifiable().remove(_lowerLinePath);
         if (false == _treeCell.getChildrenModifiable().contains(_upperLinePath))
         {
            _treeCell.getChildrenModifiable().add(_upperLinePath);
         }
      }
      else if(isOnTheLower(dragEvent))
      {
         _treeCell.getChildrenModifiable().remove(_rightLinePath);
         _treeCell.getChildrenModifiable().remove(_upperLinePath);
         if (false == _treeCell.getChildrenModifiable().contains(_lowerLinePath))
         {
            _treeCell.getChildrenModifiable().add(_lowerLinePath);
         }
      }
   }

   private boolean isOnTheRight(DragEvent dragEvent)
   {
      //if(getNodeXBegin() + (getNodeXEnd() - getNodeXBegin()) / 2 < dragEvent.getX())
      if(Math.max(getNodeXBegin() + (getNodeXEnd() - getNodeXBegin()) / 2 , getNodeXEnd() - 20) < dragEvent.getX())
      {
         return true;
      }
      else
      {
         return false;
      }
   }

   private boolean isOnTheLower(DragEvent dragEvent)
   {
      return dragEvent.getY() > _treeCell.getHeight() / 2;
   }

   private boolean isOnTheUpper(DragEvent dragEvent)
   {
      return dragEvent.getY() < _treeCell.getHeight() / 2;
   }

   private void initRightPathLine(Path path)
   {
      path.getElements().clear();

      double rightLineX = Math.min(getNodeXEnd(), _treeCell.getWidth());

      MoveTo moveTo = new MoveTo();
      moveTo.setX(rightLineX + STROKE_WIDTH);
      moveTo.setY(_treeCell.getHeight() / 2);

      LineTo lineTo = new LineTo();
      lineTo.setX(rightLineX + 10);
      lineTo.setY(_treeCell.getHeight() / 2);

      path.getElements().add(moveTo);
      path.getElements().add(lineTo);
      path.setStrokeWidth(STROKE_WIDTH);
      path.setStroke(STROKE_COLOR);
   }

   private void initHorizontalPathLine(boolean upper, Path path)
   {
      path.getElements().clear();

      MoveTo moveTo = new MoveTo();
      moveTo.setX(getNodeXBegin());

      if (upper)
      {
         moveTo.setY(0 + STROKE_WIDTH);
      }
      else
      {
         moveTo.setY(_treeCell.getHeight() - STROKE_WIDTH);
      }

      LineTo lineTo = new LineTo();
      lineTo.setX(getNodeXEnd());
      if (upper)
      {
         lineTo.setY(0 + STROKE_WIDTH);
      }
      else
      {
         lineTo.setY(_treeCell.getHeight() - STROKE_WIDTH);
      }

      path.getElements().add(moveTo);
      path.getElements().add(lineTo);
      path.setStrokeWidth(STROKE_WIDTH);
      path.setStroke(STROKE_COLOR);
   }

   private double getNodeXEnd()
   {
      double ret = Double.MIN_VALUE;

      for (Node node : _treeCell.getChildrenModifiable())
      {
         if(false == node instanceof Path)
         {
            ret = Math.max(ret, node.getLayoutX() + node.getLayoutBounds().getWidth());
         }
      }

      return ret;
   }

   private double getNodeXBegin()
   {
      double ret = Double.MAX_VALUE;

      for (Node node : _treeCell.getChildrenModifiable())
      {
         if(false == node instanceof Path)
         {
            ret = Math.min(ret, node.getLayoutX());
         }
      }

      return ret;


   }



}
