package io.grabity.planetwallet.Widgets.OverScrollWrapper;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import io.grabity.planetwallet.MiniFramework.utils.PLog;

/**
 * Created by JcobPark on 15. 8. 13..
 */
public class OverScrollWrapper extends RelativeLayout {

    private RecyclerView listView;

    public OverScrollWrapper( Context context ) {
        super( context );
        touchSlop = ViewConfiguration.get( context ).getScaledTouchSlop( );
        setWillNotDraw( false );
        setOnHierarchyChangeListener( onHierarchyChangeListener );
    }

    public OverScrollWrapper( Context context, AttributeSet attrs ) {
        super( context, attrs );
        touchSlop = ViewConfiguration.get( context ).getScaledTouchSlop( );
        setWillNotDraw( false );
        setOnHierarchyChangeListener( onHierarchyChangeListener );
    }

    OnHierarchyChangeListener onHierarchyChangeListener = new OnHierarchyChangeListener( ) {
        @Override
        public void onChildViewAdded( View parent, View child ) {
            if ( child instanceof RecyclerView ) {
                listView = ( RecyclerView ) child;
            }
        }

        @Override
        public void onChildViewRemoved( View parent, View child ) {

        }
    };

    private static final int INVALID_POINTER = 48 * 4;
    private static final float DRAG_RATE = .5f;

    private boolean refreshing = false;
    private int touchSlop;

    private float initialMotionY;
    private float initialDownY;
    private boolean isDragged;
    private int activePointerId = INVALID_POINTER;

    private boolean returningToStart;


    public boolean canChildScrollUp( ) {
        if ( this.listView != null )
            return this.listView.canScrollVertically( -1 );
        else return false;
    }


    @Override
    public boolean onInterceptTouchEvent( MotionEvent ev ) {

        final int action = ev.getActionMasked( );

        if ( returningToStart && action == MotionEvent.ACTION_DOWN ) {
            returningToStart = false;
        }

        if ( !isEnabled( ) || returningToStart || canChildScrollUp( ) || refreshing ) {
            return false;
        }

        switch ( action ) {
            case MotionEvent.ACTION_DOWN:
                activePointerId = ev.getPointerId( 0 );
                isDragged = false;
                final float initialDownY = getMotionEventY( ev, activePointerId );
                if ( initialDownY == -1 ) {
                    return false;
                }
                this.initialDownY = initialDownY;
                break;

            case MotionEvent.ACTION_MOVE:
                if ( activePointerId == INVALID_POINTER ) {
                    return false;
                }

                final float y = getMotionEventY( ev, activePointerId );
                if ( y == -1 ) {
                    return false;
                }
                final float yDiff = y - this.initialDownY;
                if ( yDiff > touchSlop && !isDragged ) {
                    initialMotionY = this.initialDownY + touchSlop;
                    isDragged = true;
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp( ev );
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isDragged = false;
                activePointerId = INVALID_POINTER;
                break;
        }

        return isDragged;
    }


    private float getMotionEventY( MotionEvent ev, int activePointerId ) {
        final int index = ev.findPointerIndex( activePointerId );
        if ( index < 0 ) {
            return -1;
        }
        return ev.getY( index );
    }


    @Override
    public boolean onTouchEvent( MotionEvent ev ) {

        final int action = ev.getActionMasked( );
        if ( returningToStart && action == MotionEvent.ACTION_DOWN ) {
            returningToStart = false;
        }

        if ( !isEnabled( ) || returningToStart || canChildScrollUp( ) ) {
            return false;
        }

        switch ( action ) {
            case MotionEvent.ACTION_DOWN:
                PLog.e( "ACTION_DOWN" );
                activePointerId = ev.getPointerId( 0 );
                isDragged = false;
                break;

            case MotionEvent.ACTION_MOVE: {

                final int pointerIndex = 0;
                ev.findPointerIndex( activePointerId );
                if ( pointerIndex < 0 ) {
                    return false;
                }

                for ( int i = 0; i < ev.getPointerCount( ); i++ ) {
                    PLog.e( "ev.getY( " + i + " ) : " + ev.getY( i ) );
                }

                final float y = ev.getY( pointerIndex );
                final float overscrollTop = ( y - initialMotionY ) * DRAG_RATE;
                if ( isDragged ) {

                    if ( this.getChildAt( 0 ) != null ) {
                        if ( this.getChildAt( 0 ).getY( ) >= 0 ) {
                            this.getChildAt( 0 ).setY( overscrollTop );
                        }
                    }
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                final int index = ev.getActionIndex( );
                activePointerId = ev.getPointerId( index );
                break;
            }

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp( ev );
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if ( activePointerId == INVALID_POINTER ) {
                    if ( action == MotionEvent.ACTION_UP ) {
                    }
                    return false;
                }
                isDragged = false;
                startRestorePosition( this.getChildAt( 0 ).getY( ) );
                refreshing = false;
                activePointerId = INVALID_POINTER;
                return false;
            }
        }

        return true;
    }


    private void onSecondaryPointerUp( MotionEvent ev ) {
        final int pointerIndex = ev.getActionIndex( );
        final int pointerId = ev.getPointerId( pointerIndex );
        if ( pointerId == activePointerId ) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            activePointerId = ev.getPointerId( newPointerIndex );
        }
    }


    private void startRestorePosition( float yPosition ) {

        ObjectAnimator animator = ObjectAnimator.ofFloat( this.getChildAt( 0 ), "y", yPosition, 0 );
        animator.setDuration( 300 );
        animator.setStartDelay( 0 );
        animator.setInterpolator( new DecelerateInterpolator( 2f ) );
        animator.addListener( new Animator.AnimatorListener( ) {
            @Override
            public void onAnimationStart( Animator animation ) {
                returningToStart = true;
            }

            @Override
            public void onAnimationEnd( Animator animation ) {
                returningToStart = false;
            }

            @Override
            public void onAnimationCancel( Animator animation ) {
                returningToStart = false;
            }

            @Override
            public void onAnimationRepeat( Animator animation ) {

            }
        } );
        animator.start( );

    }
}
